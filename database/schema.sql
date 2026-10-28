-- Eiseb Rangeland Manager database schema
-- Tested for MySQL 8.x and compatible with the original application table contracts.

CREATE DATABASE IF NOT EXISTS eiseb_rangeland
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE eiseb_rangeland;

CREATE TABLE IF NOT EXISTS users (
    user_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('staff', 'manager', 'admin') NOT NULL DEFAULT 'staff',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rangeland_camp (
    camp_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    camp_name VARCHAR(100) NOT NULL,
    location VARCHAR(120) NOT NULL,
    size_hectares DECIMAL(10,2) NOT NULL,
    grazing_capacity INT UNSIGNED NOT NULL DEFAULT 0,
    camp_type VARCHAR(50) NOT NULL,
    water_source VARCHAR(120) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (camp_id),
    UNIQUE KEY uq_rangeland_camp_name (camp_name),
    CONSTRAINT chk_camp_size_positive CHECK (size_hectares > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS grazing_rotation (
    rotation_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    camp_id INT UNSIGNED NOT NULL,
    livestock_type VARCHAR(50) NOT NULL,
    livestock_count INT UNSIGNED NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (rotation_id),
    KEY ix_grazing_camp_dates (camp_id, start_date, end_date),
    CONSTRAINT fk_grazing_camp
        FOREIGN KEY (camp_id) REFERENCES rangeland_camp(camp_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_grazing_end_after_start CHECK (end_date >= start_date)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS vegetation_monitoring (
    monitor_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    camp_id INT UNSIGNED NOT NULL,
    monitor_date DATE NOT NULL,
    vegetation_cover VARCHAR(30) NOT NULL,
    grass_height_cm DECIMAL(8,2) NOT NULL DEFAULT 0,
    recorded_by VARCHAR(100) NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (monitor_id),
    KEY ix_monitoring_camp_date (camp_id, monitor_date),
    CONSTRAINT fk_monitoring_camp
        FOREIGN KEY (camp_id) REFERENCES rangeland_camp(camp_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS water_source (
    water_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    camp_id INT UNSIGNED NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    capacity_liters DECIMAL(12,2) NOT NULL,
    last_inspected DATE NOT NULL,
    condition_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (water_id),
    KEY ix_water_camp_inspected (camp_id, last_inspected),
    CONSTRAINT fk_water_camp
        FOREIGN KEY (camp_id) REFERENCES rangeland_camp(camp_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT chk_water_capacity_positive CHECK (capacity_liters > 0)
) ENGINE=InnoDB;

-- Initial users. Replace these passwords before production use.
-- Existing plaintext passwords are upgraded automatically after the next successful login.
INSERT INTO users (full_name, username, password, role) VALUES
    ('System Administrator', 'admin', 'pbkdf2$120000$cRwkP/kHCQZgSaQt9ypelA==$392qW1R4yNl1gXlbvi4IeAaInaqInSsMwKR8YoHewv0=', 'admin'),
    ('Operations Manager', 'manager', 'pbkdf2$120000$xMfgZ9mGOykLtEeA0n0i3g==$A3UH3NXJeqT+cJesrEPmJ+92oZev3QzHaw3OzJbTFPU=', 'manager'),
    ('Field Staff', 'staff', 'pbkdf2$120000$Dg4Ee6RWpcFz/OLjprnekA==$4CNf8GurKU7JT1fKCUY+ImEvWeHQ5c49uWDTG8FImsw=', 'staff')
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), role = VALUES(role);
