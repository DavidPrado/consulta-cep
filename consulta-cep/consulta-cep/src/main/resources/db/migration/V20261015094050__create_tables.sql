CREATE TABLE cep_atendido (
    id              UUID PRIMARY KEY,
    cep             VARCHAR(8)   NOT NULL UNIQUE,
    logradouro      VARCHAR(150),
    bairro          VARCHAR(100),
    cidade          VARCHAR(100),
    uf              VARCHAR(2)   NOT NULL,
    prazo_dias      INTEGER      NOT NULL,
    taxa            NUMERIC(10,2) NOT NULL,
    criado_em       TIMESTAMP    NOT NULL,
    atualizado_em   TIMESTAMP
);

CREATE INDEX idx_cep_atendido_cep ON cep_atendido (cep);


CREATE TABLE uf_cobertura (
    id              UUID PRIMARY KEY,
    uf              VARCHAR(2)   NOT NULL UNIQUE,
    atendida        BOOLEAN      NOT NULL,
    prazo_padrao    INTEGER,
    taxa_padrao     NUMERIC(10,2),
    criado_em       TIMESTAMP    NOT NULL,
    atualizado_em   TIMESTAMP
);

CREATE TABLE consulta_log (
    id              UUID PRIMARY KEY,
    event_id        UUID         NOT NULL,
    cep             VARCHAR(8)   NOT NULL,
    origem          VARCHAR(20)  NOT NULL,
    uf              VARCHAR(2),
    cobertura       BOOLEAN,
    sucesso         BOOLEAN      NOT NULL,
    payload         JSONB,
    erro            VARCHAR(500),
    trace_id        VARCHAR(64),
    consultado_em   TIMESTAMP    NOT NULL,
    criado_em       TIMESTAMP    NOT NULL
);

CREATE INDEX idx_consulta_log_cep ON consulta_log (cep);
CREATE INDEX idx_consulta_log_consultado_em ON consulta_log (consultado_em);