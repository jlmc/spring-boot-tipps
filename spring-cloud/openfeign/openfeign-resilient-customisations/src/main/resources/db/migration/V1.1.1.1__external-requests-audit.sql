CREATE SEQUENCE IF NOT EXISTS external_request_audit_seq START WITH 1 INCREMENT BY 5;

CREATE TABLE IF NOT EXISTS external_request_audit
(
    id               BIGINT NOT NULL,
    url              VARCHAR(255),
    http_method      VARCHAR(255),
    request_body     TEXT,
    request_headers  JSONB DEFAULT '{}',
    response_body    TEXT,
    response_status  INTEGER,
    response_headers JSONB DEFAULT '{}',
    timestamp        TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_external_request_audit PRIMARY KEY (id)
);