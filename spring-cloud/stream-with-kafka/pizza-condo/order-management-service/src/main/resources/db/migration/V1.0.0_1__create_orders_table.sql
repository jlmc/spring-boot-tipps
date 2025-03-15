CREATE SEQUENCE IF NOT EXISTS orders_id_seq START 1 INCREMENT 5;


CREATE TABLE IF NOT EXISTS orders (
   --id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   id BIGINT PRIMARY KEY DEFAULT nextval('orders_id_seq'),
   placed_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
   customer_id VARCHAR NOT NULL,
   size_of INT NOT NULL DEFAULT 1,
   toppings JSONB NOT NULL DEFAULT '[]'::jsonb,
   status VARCHAR NOT NULL CHECK (status IN ('RECEIVED', 'VALIDATED', 'PAYED', 'IN_PROGRESS', 'IN_DELIVERY', 'COMPLETED', 'CANCELLED')),
   inserted_at TIMESTAMP,
   inserted_by varchar,
   last_updated_at TIMESTAMP,
   last_updated_by varchar
);

CREATE INDEX idx_orders_customer_id ON orders (customer_id);
