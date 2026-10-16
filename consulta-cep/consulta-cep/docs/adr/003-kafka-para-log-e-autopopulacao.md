# ADR 003 — Kafka para log de auditoria e auto-população da base interna

- **Status:** Aceito
- **Data:** 2026-10-16
- **Decisores:** David Stefani Prado

## Contexto

Duas necessidades surgiram durante o desenvolvimento:

### 1. Auditoria de consultas

O requisito pede que **todas as consultas externas** sejam registradas em banco de dados, com o horário e o payload de resposta. Mas o registro **não pode comprometer a performance** da resposta ao cliente.

### 2. Auto-população da base interna

Observamos que a base de CEPs atendidos só cresce quando o time interno cadastra manualmente. Mas **a grande maioria dos CEPs consultados** são de UFs já atendidas (ex: SP, RJ, MG). Fazia sentido **aproveitar** essas consultas para enriquecer a base.

## Decisão

Usamos **Apache Kafka** com **dois tópicos** dedicados:

| Tópico | Propósito | Consumer |
|---|---|---|
| `consulta-log-topic` | Auditoria de consultas | `ConsultaLogConsumer` → salva em `consulta_log` |
| `cep-descoberto-topic` | Auto-população | `CepDescobertoConsumer` → salva em `cep_atendido` |

Ambos os produtores usam `@Async` para não bloquear a resposta ao cliente.

## Alternativas consideradas

### 1. Gravação síncrona direto no banco

**Prós:** simples, atômico com a transação.
**Contras:** adicionaria ~10-30ms por consulta. Sob carga alta, o banco ficaria congestionado com INSERTs.

### 2. Filas em memória (`BlockingQueue` + `@Async`)

**Prós:** mais simples que Kafka.
**Contras:** se a aplicação reiniciar, **os eventos em fila se perdem**. Sem durabilidade.

### 3. Kafka só para auditoria (não para auto-população)

**Prós:** menos complexidade.
**Contras:** abriria mão da otimização mais impactante — a base interna não cresceria com o uso.

### 4. Gravação assíncrona via `@Async` direto no banco

**Prós:** sem Kafka.
**Contras:** sem retry automático, sem persistência em caso de falha, sem possibilidade de escalar consumidores independentemente.

## Consequências

### Positivas

- **Resposta ao cliente em ~1ms** independente do banco.
- **Durabilidade**: se a aplicação cair, os eventos persistem no Kafka.
- **Desacoplamento**: múltiplos consumers podem ser adicionados.
- **Idempotência**: o `CepDescobertoConsumer` verifica `existsByCep` antes de inserir.
- **Auto-população transparente**: a base interna cresce sem trabalho manual.
- **Auditoria completa** com payload completo do ViaCEP.

### Negativas

- **Maior complexidade** (2 tópicos, 2 consumers, 2 publishers).
- **Possível inconsistência eventual**: o log pode aparecer no banco alguns milissegundos após a resposta ao cliente.
- **Necessidade de Kafka rodando** para funcionalidade completa (embora a app não quebre se ele estiver fora — os publishers fazem `try/catch`).
- **Debug mais difícil** (fluxo distribuído entre threads).

## Mitigação das negativas

- **Fallback silencioso**: se o Kafka estiver fora, a aplicação continua respondendo (o log/auto-população é perdido temporariamente).
- **Logs estruturados** com `traceId` para rastrear cada evento.
- **Endpoints de inspeção** (`/api/v1/auditoria/logs`) para verificar o estado.

## Referências

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- Martin Kleppmann, *Designing Data-Intensive Applications* (Capítulo 11)
- Chris Richardson, [Transactional Outbox Pattern](https://microservices.io/patterns/data/transactional-outbox.html)