SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(128),
    phone VARCHAR(32),
    status INT NOT NULL DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME,
    role VARCHAR(20) NOT NULL DEFAULT 'patient',
    UNIQUE KEY uk_user_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS userinfo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userid INT,
    realname VARCHAR(64),
    gender INT,
    avatar VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS department (
    department_id VARCHAR(32) PRIMARY KEY,
    department_name VARCHAR(128) NOT NULL,
    introduction VARCHAR(512),
    location VARCHAR(128),
    phone VARCHAR(32)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64),
    name VARCHAR(64) NOT NULL,
    department_id VARCHAR(32),
    title VARCHAR(64),
    userinfo_id INT,
    KEY idx_doctor_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    gender VARCHAR(8),
    age INT,
    address VARCHAR(255),
    id_card VARCHAR(32),
    contact VARCHAR(32),
    create_time DATETIME,
    doctor_id INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS medical_record (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    chief_complaint VARCHAR(512),
    present_illness TEXT,
    past_history TEXT,
    physical_exam TEXT,
    auxiliary_exam TEXT,
    diagnosis VARCHAR(512),
    entry_time DATETIME,
    KEY idx_record_patient (patient_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS doctor_schedule (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    schedule_date DATE NOT NULL,
    shift_type VARCHAR(32),
    start_time VARCHAR(16),
    end_time VARCHAR(16),
    department_id VARCHAR(32),
    room_number VARCHAR(32),
    max_patients INT NOT NULL DEFAULT 0,
    remaining_slots INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    KEY idx_schedule_doctor (doctor_id, schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS consulting_room (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    department_id VARCHAR(32),
    room_number VARCHAR(32),
    room_type VARCHAR(32),
    capacity INT,
    location VARCHAR(128),
    status INT DEFAULT 1,
    remark VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS drug (
    drug_id INT AUTO_INCREMENT PRIMARY KEY,
    drug_name VARCHAR(128) NOT NULL,
    drug_type VARCHAR(64),
    indication VARCHAR(512),
    applicable_department VARCHAR(128),
    dosage_form VARCHAR(64),
    specification VARCHAR(128),
    manufacturer VARCHAR(128),
    unit_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    stock_quantity INT NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    CONSTRAINT chk_drug_stock CHECK (stock_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS simple_reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    department_id VARCHAR(32),
    doctor_id INT NOT NULL,
    patient_id INT,
    schedule_id INT,
    patient_name VARCHAR(64),
    patient_age INT,
    patient_phone VARCHAR(32),
    reservation_status INT NOT NULL DEFAULT 1,
    visit_status INT NOT NULL DEFAULT 0,
    register_status INT NOT NULL DEFAULT 0,
    creat_time DATETIME,
    KEY idx_reservation_doctor (doctor_id, register_status),
    KEY idx_reservation_schedule (schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prescription (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    register_id INT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_date DATETIME,
    drug_info TEXT,
    diagnosis VARCHAR(512),
    total_fee DECIMAL(10, 2) NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 0 COMMENT '0 待收费 1 已收费 2 已发药',
    KEY idx_prescription_doctor (doctor_id),
    KEY idx_prescription_patient (patient_id),
    KEY idx_prescription_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
