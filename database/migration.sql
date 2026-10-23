-- 已有数据库升级脚本（执行一次）
-- 新建数据库请直接使用 schema.sql

ALTER TABLE doctor_schedule ADD COLUMN remaining_slots INT NOT NULL DEFAULT 0;
UPDATE doctor_schedule SET remaining_slots = max_patients WHERE remaining_slots = 0;

ALTER TABLE simple_reservation ADD COLUMN schedule_id INT NULL;
ALTER TABLE simple_reservation ADD COLUMN register_status INT NOT NULL DEFAULT 0;
UPDATE simple_reservation SET register_status = CASE WHEN visit_status = 1 THEN 2 ELSE 0 END;

ALTER TABLE prescription ADD COLUMN register_id INT NULL;
UPDATE prescription SET status = 0 WHERE status IS NULL;

UPDATE drug SET stock_quantity = 0 WHERE stock_quantity < 0;
ALTER TABLE drug ADD CONSTRAINT chk_drug_stock CHECK (stock_quantity >= 0);

CREATE TABLE IF NOT EXISTS prescription_item (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    KEY idx_item_prescription (prescription_id),
    CONSTRAINT chk_item_quantity CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS charge_record (
    charge_id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    charge_time DATETIME NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    cashier_id INT,
    UNIQUE KEY uk_charge_prescription (prescription_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    operator_id INT,
    operator_name VARCHAR(64),
    role VARCHAR(20),
    action VARCHAR(32) NOT NULL,
    detail VARCHAR(1024),
    created_time DATETIME NOT NULL,
    KEY idx_audit_action (action),
    KEY idx_audit_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
