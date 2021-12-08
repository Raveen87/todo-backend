CREATE SCHEMA IF NOT EXISTS TODO;

CREATE TABLE IF NOT EXISTS TODO_ITEM (
   id           BIGSERIAL PRIMARY KEY,
   title        TEXT,
   completed    BOOLEAN,
   sort_order   BIGINT
);