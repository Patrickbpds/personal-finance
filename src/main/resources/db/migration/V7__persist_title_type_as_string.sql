ALTER TABLE finance_tables.title
    DROP CONSTRAINT chk_title_type_valid;

ALTER TABLE finance_tables.title
    ALTER COLUMN type TYPE VARCHAR(20)
    USING CASE type
        WHEN 0 THEN 'RECEIVABLE'
        WHEN 1 THEN 'PAYABLE'
    END;

ALTER TABLE finance_tables.title
    ALTER COLUMN type SET NOT NULL;

ALTER TABLE finance_tables.title
    ADD CONSTRAINT chk_title_type_valid
        CHECK (type IN ('RECEIVABLE', 'PAYABLE'));

COMMENT ON COLUMN finance_tables.title.type IS 'Title type: RECEIVABLE or PAYABLE';
