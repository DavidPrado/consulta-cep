# ADR 004 — Virtual Threads no motor de lote

- **Status:** Aceito
- **Data:** 2026-10-16
- **Decisores:** David Stefani Prado

## Contexto

Uma loja virtual pode ter **1000+ CEPs** de clientes para processar em lote (ex: importação de pedidos, geração de relatórios, atualização em massa).

A primeira implementação do motor de lote era **sequencial**:

```text
A cada 10s:
  Pega 50 CEPs PENDENTE
  Para cada CEP:
    Consulta via BuscarCepService (~200ms)
  Salva 50 itens individualmente