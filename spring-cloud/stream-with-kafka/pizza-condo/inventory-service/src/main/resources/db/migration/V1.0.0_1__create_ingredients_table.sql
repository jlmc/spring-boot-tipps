CREATE SEQUENCE IF NOT EXISTS ingredients_id_seq START 1 INCREMENT 5;


CREATE TABLE IF NOT EXISTS ingredients (
   --id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   id BIGINT PRIMARY KEY DEFAULT nextval('ingredients_id_seq'),
   name VARCHAR UNIQUE NOT NULL,
   qty INTEGER DEFAULT 0 NOT NULL,
   inserted_at TIMESTAMP DEFAULT now(),
   inserted_by VARCHAR,
   last_updated_at TIMESTAMP,
   last_updated_by VARCHAR,
   version SMALLINT NOT NULL DEFAULT 0
);

