-- Create schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS finance_tables;

-- Create user table
CREATE TABLE finance_tables.user (
    id_user UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    email VARCHAR(80) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    photo TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    inactivation_date TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_name_length CHECK (LENGTH(name) >= 3),
    CONSTRAINT chk_email_length CHECK (LENGTH(email) >= 6),
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create indexes for better performance
CREATE INDEX idx_user_email ON finance_tables.user(email);
CREATE INDEX idx_user_registration_date ON finance_tables.user(registration_date);
CREATE INDEX idx_user_inactivation_date ON finance_tables.user(inactivation_date);

-- Comments for documentation
COMMENT ON TABLE finance_tables.user IS 'User table for personal finance application';
COMMENT ON COLUMN finance_tables.user.id_user IS 'Primary key - UUID';
COMMENT ON COLUMN finance_tables.user.name IS 'User full name (3-80 characters)';
COMMENT ON COLUMN finance_tables.user.email IS 'User email address (unique, 6-80 characters)';
COMMENT ON COLUMN finance_tables.user.password IS 'Encrypted user password';
COMMENT ON COLUMN finance_tables.user.photo IS 'User profile photo (base64 or URL)';
COMMENT ON COLUMN finance_tables.user.registration_date IS 'User registration timestamp';
COMMENT ON COLUMN finance_tables.user.inactivation_date IS 'User inactivation timestamp (null if active)';