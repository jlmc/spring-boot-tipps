
ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS version smallint default 1;
