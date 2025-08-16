-- Remove audit "by" fields from user table

-- Drop indexes first
DROP INDEX IF EXISTS finance_tables.idx_user_created_by;
DROP INDEX IF EXISTS finance_tables.idx_user_updated_by;

-- Remove columns
ALTER TABLE finance_tables.user
DROP COLUMN IF EXISTS created_by,
    DROP COLUMN IF EXISTS updated_by;