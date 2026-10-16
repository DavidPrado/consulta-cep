# ADR 002 — Circuit Breaker manual em vez de Resilience4j

- **Status:** Aceito
- **Data:** 2026-10-16
- **Decisores:** David Stefani Prado

## Contexto

O ViaCEP é uma API externa dos Correios, usada como fallback quando um CEP não está na base interna. Como qualquer API externa, pode ficar lenta, indisponível ou responder com erro.

**Risco:** se o ViaCEP fica fora do ar e centenas de requisições chegam ao mesmo tempo, todas as threads do Tomcat ficam presas esperando timeout. Resultado: **a aplicação inteira para**, mesmo para endpoints que não dependem do ViaCEP (como cadastro de CEPs, listagem, health check).

**Padrão clássico:** Circuit Breaker.

**Biblioteca padrão:** Resilience4j.

## Decisão

Implementamos o padrão Circuit Breaker **manualmente**, sem dependência do Resilience4j.

A implementação usa:
- **Máquina de estados** com 3 estados: `CLOSED`, `OPEN`, `HALF_OPEN`.
- **Janela deslizante** com `ArrayDeque` para contabilizar sucessos e falhas.
- **`synchronized`** para garantir thread-safety.
- **`Instant`** para controlar o tempo em estado `OPEN`.

Adicionalmente, implementamos:
- **Bulkhead** com `Semaphore` (limita concorrência).
- **Retry com backoff exponencial** (3 tentativas: 1s → 2s → 4s).
- **Fallback** para resposta degradada.

## Alternativas consideradas

### 1. Resilience4j 2.x com Spring Boot 3

**Prós:** biblioteca madura, amplamente usada, menos código.
**Contras:** foi a primeira tentativa e **falhou**. O Spring Boot 4.1.1 usa Spring Framework 7, e o Resilience4j ainda não lançou uma versão estável para essa combinação. Erros como `resilience4j-spring-boot3` não encontrado e `spring-boot-starter-aop` incompatível bloquearam a abordagem.

### 2. Resilience4j com downgrade do Spring Boot

**Prós:** funcionaria.
**Contras:** abriria mão dos recursos modernos do Spring Boot 4 (Virtual Threads, novo AOT, novas versões de JPA). Inaceitável.

### 3. Não implementar Circuit Breaker

**Prós:** mais simples.
**Contras:** violaria o requisito de resiliência. A aplicação poderia travar em produção.

## Consequências

### Positivas

- **Zero dependência externa** para resiliência.
- **Controle total** sobre comportamento (podemos ajustar sem upgrade de lib).
- **Entendimento profundo** do padrão (útil em entrevistas sênior).
- **Compatível com Spring Boot 4.1.1** sem gambiarras.
- **Integração com Actuator** via endpoint customizado `/api/v1/operacoes/circuit-breakers`.

### Negativas

- **Mais código para manter** (~200 linhas em `shared/resilience/`).
- **Sem features avançadas** que o Resilience4j oferece (métricas Micrometer automáticas, rate limiter, time limiter, etc.).
- **Responsabilidade de testar** a implementação manualmente.

## Mitigação das negativas

- Código **isolado em `shared/resilience/`** — fácil de trocar por Resilience4j no futuro.
- **Testes unitários** cobrindo cada transição de estado.
- **Endpoint de inspeção** (`/api/v1/operacoes/circuit-breakers`) para monitoramento.

## Referências

- Martin Fowler, [CircuitBreaker](https://martinfowler.com/bliki/CircuitBreaker.html)
- Michael Nygard, *Release It!*
- [Resilience4j no GitHub](https://github.com/resilience4j/resilience4j)