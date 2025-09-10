-- Create title_center_costs junction table for many-to-many relationship
CREATE TABLE finance_tables.title_center_costs (
    idTitle UUID NOT NULL,
    idCenterCost UUID NOT NULL,

    -- Primary key (composite)
    PRIMARY KEY (idTitle, idCenterCost),

    -- Foreign key constraints
    CONSTRAINT fk_title_center_costs_title
       FOREIGN KEY (idTitle)
           REFERENCES finance_tables.title(idTitle)
           ON DELETE CASCADE,

    CONSTRAINT fk_title_center_costs_center_cost
       FOREIGN KEY (idCenterCost)
           REFERENCES finance_tables.center_costs(idCenterCost)
           ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_title_center_costs_title ON finance_tables.title_center_costs(idtitle);
CREATE INDEX idx_title_center_costs_center_cost ON finance_tables.title_center_costs(idcentercost);

-- Comments for documentation
COMMENT ON TABLE finance_tables.title_center_costs IS 'Junction table for many-to-many relationship between titles and center costs';
COMMENT ON COLUMN finance_tables.title_center_costs.idtitle IS 'Foreign key to title table';
COMMENT ON COLUMN finance_tables.title_center_costs.idcentercost IS 'Foreign key to center_costs table';