CREATE TABLE IF NOT EXISTS todo_item (
   id           BIGSERIAL PRIMARY KEY,
   title        TEXT,
   completed    BOOLEAN,
   sort_order   BIGINT
);
