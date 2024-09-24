CREATE TABLE IF NOT EXISTS external_request_audit
(
    id               BIGINT NOT NULL,
    url              VARCHAR(255),
    request_instant  TIMESTAMP WITHOUT TIME ZONE,
    http_method      VARCHAR(255),
    request_body     TEXT,
    request_headers  JSONB DEFAULT '{}',
    response_body    TEXT,
    response_status  INTEGER,
    response_headers JSONB DEFAULT '{}',
    response_instant TIMESTAMP WITHOUT TIME ZONE,
    elapsed_time     bigint,

    CONSTRAINT pk_external_request_audit PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS external_request_audit_seq START WITH 1 INCREMENT BY 5;
