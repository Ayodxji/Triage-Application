-- Run this once to add the assignments table.
-- The table stores the output of each scheduling algorithm run.

CREATE TABLE IF NOT EXISTS assignments (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nurse_id    VARCHAR(255) NOT NULL,
    patient_id  VARCHAR(255) NOT NULL,
    assigned_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (nurse_id)   REFERENCES nurse(nurse_id)   ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patient(patient_id) ON DELETE CASCADE
);
