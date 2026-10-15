# Consulta CEP - Cobertura de Entrega

Sistema de cobertura de entrega que permite cadastrar CEPs atendidos e UFs atendidas.
Ao consultar um CEP, o sistema busca primeiro na base interna; se não encontrar,
consulta a API ViaCEP, verifica se a UF é atendida e registra o log da consulta
de forma assíncrona via Kafka.

## Stack

- Java 21
- Spring Boot 3.3+
- PostgreSQL 16
- Apache Kafka (KRaft)
- Flyway
- Docker + Docker Compose

## Como rodar

```bash
docker-compose up --build

## Endpoints
POST /api/v1/ceps-atendidos
GET /api/v1/ceps-atendidos
POST /api/v1/ufs-cobertura
PUT /api/v1/ufs-cobertura/{uf}
GET /api/v1/ufs-cobertura
GET /api/v1/cep/{cep}
GET /api/v1/logs
