-- Create center_costs table
CREATE TABLE finance_tables.center_costs (
     idCenterCost UUID DEFAULT gen_random_uuid() PRIMARY KEY,
     description VARCHAR(100) NOT NULL,
     observation TEXT,
     idUser UUID NOT NULL,

    -- Foreign key constraints
     CONSTRAINT fk_center_cost_user
         FOREIGN KEY (idUser)
             REFERENCES finance_tables.user(id_user)
             ON DELETE CASCADE,

    -- Check constraints
     CONSTRAINT chk_center_cost_description_length
         CHECK (LENGTH(description) >= 3),
     CONSTRAINT chk_center_cost_observation_length
         CHECK (observation IS NULL OR LENGTH(observation) <= 500)
);

-- Create indexes for better performance
CREATE INDEX idx_center_costs_user ON finance_tables.center_costs(idUser);
CREATE INDEX idx_center_costs_description ON finance_tables.center_costs(description);

-- Comments for documentation
COMMENT ON TABLE finance_tables.center_costs IS 'Center costs table for expense categorization';
COMMENT ON COLUMN finance_tables.center_costs.idCenterCost IS 'Primary key - UUID';
COMMENT ON COLUMN finance_tables.center_costs.description IS 'Center cost description (3-100 characters)';
COMMENT ON COLUMN finance_tables.center_costs.observation IS 'Optional observation (max 500 characters)';
COMMENT ON COLUMN finance_tables.center_costs.idUser IS 'Foreign key to user table';