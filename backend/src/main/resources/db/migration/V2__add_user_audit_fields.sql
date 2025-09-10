-- Add audit fields to user table
ALTER TABLE finance_tables.user
    ADD COLUMN created_by UUID,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_by UUID;

-- Add index for updated_at
CREATE INDEX idx_user_updated_at ON finance_tables.user(updated_at);
CREATE INDEX idx_user_created_by ON finance_tables.user(created_by);
CREATE INDEX idx_user_updated_by ON finance_tables.user(updated_by);

-- Comments
COMMENT ON COLUMN finance_tables.user.created_by IS 'User ID who created this record';
COMMENT ON COLUMN finance_tables.user.updated_at IS 'Last update timestamp';
COMMENT ON COLUMN finance_tables.user.updated_by IS 'User ID who last updated this record';