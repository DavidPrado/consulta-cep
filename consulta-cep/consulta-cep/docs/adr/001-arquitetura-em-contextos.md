# ADR 001 — Arquitetura em 4 contextos

- **Status:** Aceito
- **Data:** 2026-10-16
- **Decisores:** David Stefani Prado

## Contexto

O projeto começou com uma estrutura de pacotes tradicional (`application`, `domain`, `infrastructure`, `interfaces`). Conforme o código cresceu — com consulta de CEP, cadastro de CEPs atendidos, cadastro de UFs, logs de auditoria, circuit breaker, bulkhead, motor de lote — ficou difícil navegar e entender o que cada classe fazia.

Os endpoints estavam misturados (`/api/v1/cep`, `/api/v1/ceps-atendidos`, `/api/v1/logs`, `/api/v1/resilience`), sem uma lógica clara de agrupamento.

## Decisão

Reorganizamos o projeto em **4 bounded contexts** inspirados em Domain-Driven Design (DDD):

| Contexto | Responsabilidade | URL |
|---|---|---|
| **Consulta** | Consultar um CEP (público) | `/api/v1/consulta/{cep}` |
| **Cobertura** | Gerenciar CEPs, UFs e lotes (admin) | `/api/v1/cobertura/*` |
| **Auditoria** | Visualizar logs de consulta | `/api/v1/auditoria/logs` |
| **Operações** | Monitorar resiliência e cache | `/api/v1/operacoes/*` |

Adicionalmente, criamos um módulo `shared/` com código reutilizável (exceptions, resilience, sanitizador, publishers Kafka).

## Alternativas consideradas

### 1. Manter estrutura tradicional (application/domain/infrastructure/interfaces)

**Prós:** convenção amplamente conhecida, menos mudança.
**Contras:** com o crescimento, ficou confuso. Classes de contextos diferentes convivem nas mesmas pastas.

### 2. Arquitetura hexagonal (Ports and Adapters) pura

**Prós:** muito rigorosa, testável.
**Contras:** overhead alto para o tamanho do projeto. Mistura com DDD light seria mais pragmático.

### 3. Microsserviços separados

**Prós:** isolamento total.
**Contras:** overkill. Deploy, observabilidade e testes ficariam complexos demais para um desafio técnico.

## Consequências

### Positivas

- **Código conta a mesma história das URLs.** Para entender "cobertura", você abre a pasta `cobertura/`.
- **Cada contexto pode virar um microsserviço no futuro** sem refactor massivo.
- **Fácil navegação** para novos desenvolvedores.
- **Alinhamento com a linguagem do negócio** (ubiquitous language).
- **Isolamento de mudanças**: alterar o ViaCEP afeta só `consulta/`.

### Negativas

- **Overhead inicial** de criar pastas em vez de uma única `application/`.
- **Risco de over-engineering** se mal aplicado.
- **Curva de aprendizado** para quem nunca viu DDD.

## Referências

- Eric Evans, *Domain-Driven Design: Tackling Complexity in the Heart of Software*
- Vaughn Vernon, *Implementing Domain-Driven Design*
- Martin Fowler, [Bounded Context](https://martinfowler.com/bliki/BoundedContext.html)