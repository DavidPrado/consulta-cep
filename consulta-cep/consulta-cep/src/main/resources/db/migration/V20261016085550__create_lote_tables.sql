CREATE TABLE lote_cep (
                          id              UUID PRIMARY KEY,
                          status          VARCHAR(20)   NOT NULL,
                          total           INTEGER       NOT NULL,
                          processados     INTEGER       NOT NULL DEFAULT 0,
                          pendentes       INTEGER       NOT NULL DEFAULT 0,
                          falhas          INTEGER       NOT NULL DEFAULT 0,
                          criado_em       TIMESTAMP     NOT NULL,
                          atualizado_em   TIMESTAMP
);

CREATE INDEX idx_lote_cep_status ON lote_cep (status);

CREATE TABLE lote_cep_item (
                               id                  UUID PRIMARY KEY,
                               lote_id             UUID         NOT NULL,
                               cep                 VARCHAR(8)   NOT NULL,
                               status              VARCHAR(20)  NOT NULL,
                               dados               JSONB,
                               erro                VARCHAR(500),
                               tentativas          INTEGER      NOT NULL DEFAULT 0,
                               ultima_tentativa    TIMESTAMP,
                               criado_em           TIMESTAMP    NOT NULL,
                               atualizado_em       TIMESTAMP,
                               CONSTRAINT fk_lote_cep_item_lote FOREIGN KEY (lote_id) REFERENCES lote_cep(id) ON DELETE CASCADE
);

CREATE INDEX idx_lote_cep_item_lote_id ON lote_cep_item (lote_id);
CREATE INDEX idx_lote_cep_item_status ON lote_cep_item (status);
CREATE INDEX idx_lote_cep_item_cep ON lote_cep_item (cep);