-- Create title table
CREATE TABLE finance_tables.title (
    idTitle UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    idUser UUID NOT NULL,
    type INTEGER NOT NULL,
    value DECIMAL(15,2) NOT NULL,
    registrationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    referenceDate TIMESTAMP,
    expirationDate TIMESTAMP NOT NULL,
    paymentDate TIMESTAMP,
    observation TEXT,

    -- Foreign key constraints
    CONSTRAINT fk_title_user
      FOREIGN KEY (idUser)
          REFERENCES finance_tables.user(id_user)
          ON DELETE CASCADE,

    -- Check constraints
    CONSTRAINT chk_title_description_length
      CHECK (LENGTH(description) >= 3),
    CONSTRAINT chk_title_value_positive
      CHECK (value > 0),
    CONSTRAINT chk_title_type_valid
      CHECK (type IN (0, 1)), -- 0 = RECEIVABLE, 1 = PAYABLE
    CONSTRAINT chk_title_observation_length
      CHECK (observation IS NULL OR LENGTH(observation) <= 500)
);

-- Create indexes for better performance
CREATE INDEX idx_title_user ON finance_tables.title(iduser);
CREATE INDEX idx_title_type ON finance_tables.title(type);
CREATE INDEX idx_title_registration_date ON finance_tables.title(registrationdate);
CREATE INDEX idx_title_reference_date ON finance_tables.title(referencedate);
CREATE INDEX idx_title_expiration_date ON finance_tables.title(expirationdate);
CREATE INDEX idx_title_payment_date ON finance_tables.title(paymentdate);

-- Comments for documentation
COMMENT ON TABLE finance_tables.title IS 'Financial titles table (receivables and payables)';
COMMENT ON COLUMN finance_tables.title.idtitle IS 'Primary key - UUID';
COMMENT ON COLUMN finance_tables.title.description IS 'Title description (3-100 characters)';
COMMENT ON COLUMN finance_tables.title.iduser IS 'Foreign key to user table';
COMMENT ON COLUMN finance_tables.title.type IS 'Title type: 0=RECEIVABLE, 1=PAYABLE';
COMMENT ON COLUMN finance_tables.title.value IS 'Title value (positive decimal)';
COMMENT ON COLUMN finance_tables.title.registrationdate IS 'Title registration timestamp';
COMMENT ON COLUMN finance_tables.title.referencedate IS 'Reference date for the title';
COMMENT ON COLUMN finance_tables.title.expirationdate IS 'Title expiration date (REQUIRED)';
COMMENT ON COLUMN finance_tables.title.paymentdate IS 'Actual payment date (null if not paid)';
COMMENT ON COLUMN finance_tables.title.observation IS 'Optional observation (max 500 characters)';