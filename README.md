# 🚚 Consulta CEP — Motor de Cobertura de Entregas

> API REST que responde à pergunta: **"Essa loja entrega no meu CEP?"**

Motor de consulta de cobertura de entregas para e-commerce, com priorização de base interna, fallback para API externa (ViaCEP), auditoria assíncrona via Kafka e resiliência de nível de produção.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![Apache Kafka](https://img.shields.io/badge/Kafka-4.2-black?logo=apachekafka)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://www.docker.com/)

---

## 📖 Visão Geral

O sistema resolve um problema real de e-commerce: **verificar se um CEP é atendido e com qual prazo/taxa**, sem depender exclusivamente de uma API externa.

### Como funciona

1. **Cliente final** consulta um CEP pelo endpoint público.
2. **Motor de busca** verifica primeiro a **base interna** (CEPs que a loja já cadastrou).
3. Se **não encontrar**, consulta a API externa **ViaCEP**.
4. Verifica se a **UF** é atendida pela loja e retorna prazo + taxa.
5. **Auditoria**: cada consulta externa gera um log via Kafka (assíncrono, sem afetar a resposta).
6. **Auto-população**: CEPs descobertos de UFs atendidas são salvos na base interna automaticamente.

### Por que ele é diferente

| Recurso | Implementação |
|---|---|
| **Priorização interna** | Base local antes de gastar chamada externa |
| **Resiliência** | Circuit Breaker + Bulkhead + Retry + Fallback |
| **Performance** | Cache, paginação, `@Async`, Virtual Threads |
| **Auto-população** | Base interna cresce com o uso real |
| **Processamento em lote** | 1000+ CEPs com Virtual Threads, sem perder dados |
| **Auditoria assíncrona** | Kafka para logs, sem bloquear resposta |

---

## 🛠 Stack Tecnológica

### Backend
- **Java 21** (Virtual Threads, Records, Pattern Matching)
- **Spring Boot 4.1.1**
- **Spring Data JPA** + **Hibernate 7**
- **PostgreSQL 16**
- **Apache Kafka 4.2** (producer + consumer)
- **Flyway** (migrations)
- **Caffeine** (cache em memória)
- **Jackson 3**

### Infraestrutura
- **Docker + Docker Compose**
- **Kafka em modo KRaft** (sem Zookeeper)

### Bibliotecas de suporte
- **Lombok** (produtividade)
- **Bean Validation** (validação)
- **Spring Actuator** (observabilidade)
- **SLF4J + Logback** (logs)

### Testes
- **JUnit 5**
- **Mockito**
- **AssertJ**

---

## 🏗 Arquitetura

O projeto segue uma arquitetura **inspirada em DDD (Domain-Driven Design)** com **4 bounded contexts** + um módulo **shared**.

### Diagrama de Contextos

```mermaid
flowchart TB
    subgraph Cliente["👤 Consumidores"]
        C1["Cliente Final"]
        C2["Time Interno"]
        C3["Auditoria"]
        C4["DevOps"]
    end

    subgraph API["🚚 Motor de Cobertura de Entregas"]
        direction TB
        
        subgraph Consulta["🌐 Contexto: Consulta"]
            A1["GET /api/v1/consulta/:cep"]
        end
        
        subgraph Cobertura["🛠 Contexto: Cobertura"]
            B1["CRUD /api/v1/cobertura/ceps"]
            B2["CRUD /api/v1/cobertura/ufs"]
            B3["POST /api/v1/lotes"]
        end
        
        subgraph Auditoria["📋 Contexto: Auditoria"]
            D1["GET /api/v1/auditoria/logs"]
        end
        
        subgraph Operacoes["⚙️ Contexto: Operações"]
            E1["GET /api/v1/operacoes/*"]
        end
        
        subgraph Shared["🔧 Shared"]
            S1["Circuit Breaker"]
            S2["Bulkhead"]
            S3["Sanitizador"]
            S4["Kafka Publishers"]
        end
    end

    subgraph Infra["🗄 Infraestrutura"]
        DB[("PostgreSQL")]
        KF[("Kafka")]
        VC["ViaCEP"]
    end

    C1 --> A1
    C2 --> B1
    C2 --> B2
    C2 --> B3
    C3 --> D1
    C4 --> E1

    A1 --> Shared
    B1 --> Shared
    B2 --> Shared
    B3 --> Shared
    Shared --> DB
    Shared --> KF
    A1 --> VC
```

### Estrutura de Pacotes

```
src/main/java/br/com/cobertura/consulta/
├── ConsultaCepApplication.java
│
├── consulta/                    # Contexto 1 — Consulta (público)
│   ├── api/                     # CepConsultaController
│   ├── application/             # BuscarCepService, DTOs, UseCases
│   └── infrastructure/viacep/   # ViaCepProvider
│
├── cobertura/                   # Contexto 2 — Cobertura (admin)
│   ├── api/                     # Controllers de CEP, UF, Lote
│   ├── application/             # Services, DTOs, UseCases
│   ├── domain/                  # Entidades + Repositórios
│   └── infrastructure/          # Kafka consumers + worker
│
├── auditoria/                   # Contexto 3 — Auditoria
│   ├── api/
│   ├── application/
│   ├── domain/                  # ConsultaLog
│   └── infrastructure/kafka/    # Consumer
│
├── operacoes/                   # Contexto 4 — Operações
│   └── api/                     # Resilience, Cache, Worker
│
└── shared/                      # Código compartilhado
    ├── config/                  # Configs (Kafka, Async, VirtualThreads)
    ├── exception/               # Exceptions + GlobalExceptionHandler
    ├── messaging/               # Publishers Kafka
    ├── model/                   # OrigemConsulta
    ├── port/out/                # Interfaces (CepProvider, Publishers)
    ├── resilience/              # CircuitBreaker, Bulkhead
    └── service/                 # Sanitizador
```

### Diagrama de Fluxo — Consulta de CEP

```mermaid
sequenceDiagram
    autonumber
    participant U as Cliente
    participant API as CepConsultaController
    participant S as BuscarCepService
    participant R as CepAtendidoRepository
    participant CB as CircuitBreaker
    participant BH as Bulkhead
    participant VC as ViaCEP
    participant K as Kafka
    participant C as Consumers

    U->>API: GET /api/v1/consulta/{cep}
    API->>S: buscar(cep)
    S->>S: Sanitiza + valida
    S->>R: findByCep(cep)

    alt CEP na base interna
        R-->>S: CepAtendido
        S-->>U: origem=INTERNO (rápido ~16ms)
    else CEP não encontrado
        S->>CB: allowRequest?
        alt CB OPEN
            CB-->>S: false
            S-->>U: fallback
        else CB CLOSED/HALF_OPEN
            S->>BH: tryAcquire?
            alt BH lotado
                S-->>U: fallback
            else BH disponível
                S->>VC: buscar(cep)
                VC-->>S: endereço
                S->>K: publica ConsultaLogEvent
                S->>K: publica CepDescobertoEvent (se UF atendida)
                S-->>U: origem=EXTERNO + cobertura
                K->>C: log consumer
                K->>C: cep-descoberto consumer
                C->>R: save (auto-popula)
            end
        end
    end
```

---

## 🚀 Como Rodar

### Pré-requisitos

- **Docker** e **Docker Compose**
- **Java 21** (apenas se for rodar localmente sem Docker)
- **Maven** (opcional — use `mvnw`)

### Opção 1 — Docker Compose (Recomendado)

```bash
# Clone o repositório
git clone https://github.com/DavidPrado/consulta-cep.git
cd consulta-cep

# Suba tudo (app + PostgreSQL + Kafka)
docker-compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

**Tempo de inicialização:** ~3 a 5 minutos na primeira execução (build do Maven).

### Opção 2 — Rodar Localmente (Desenvolvimento)

```bash
# 1. Suba apenas a infraestrutura
docker-compose up postgres kafka -d

# 2. Rode a aplicação
./mvnw spring-boot:run

# 3. Acesse
# http://localhost:8088/actuator/health
```

### Verificar se está funcionando

```bash
curl http://localhost:8088/actuator/health
# {"status":"UP"}
```

---

## 🧪 Testes

```bash
./mvnw test
```

A suíte inclui:
- Testes unitários dos Services
- Testes do Sanitizador
- Testes de Controller (`@WebMvcTest`)
- Teste de integração do ViaCEP (`ViaCepProviderIT`)

---

## 📦 Estrutura do Repositório

```
consulta-cep/
├── .github/                     # GitHub Actions (futuro)
├── docs/
│   ├── adr/                     # Architecture Decision Records
│   └── consulta-cep.postman_collection.json
├── src/
│   ├── main/
│   │   ├── java/br/com/cobertura/consulta/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-docker.yml
│   │       └── db/migration/
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```