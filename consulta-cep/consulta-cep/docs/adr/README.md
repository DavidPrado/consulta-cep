# Architecture Decision Records (ADRs)

Este diretório contém as decisões arquiteturais significativas do projeto.

## O que é um ADR?

Um ADR é um documento curto que registra **uma decisão arquitetural importante**, o **contexto** em que ela foi tomada, as **alternativas consideradas** e as **consequências** da escolha.

## Formato

Cada ADR segue a estrutura:
- **Status**: Proposto, Aceito, Rejeitado, Depreciado ou Substituído
- **Contexto**: qual problema estamos resolvendo
- **Decisão**: o que foi decidido
- **Alternativas consideradas**: o que foi descartado e por quê
- **Consequências**: o que isso traz de positivo e negativo

## Índice

| # | Título | Status |
|---|---|---|
| [001](001-arquitetura-em-contextos.md) | Arquitetura em 4 contextos | Aceito |
| [002](002-circuit-breaker-manual.md) | Circuit Breaker manual | Aceito |
| [003](003-kafka-para-log-e-autopopulacao.md) | Kafka para log e auto-população | Aceito |
| [004](004-virtual-threads-no-lote.md) | Virtual Threads no motor de lote | Aceito |


### 📮 Collection do Postman

O repositório inclui uma collection completa com todos os endpoints prontos:

1. Abra o Postman.
2. Clique em **Import**.
3. Selecione o arquivo `docs/consulta-cep.postman_collection.json`.
4. A collection **Consulta CEP** aparecerá com as requisições organizadas por contexto.

As requisições incluem testes automatizados (scripts) que verificam os status HTTP e salvam automaticamente o `loteId` após criar um lote.