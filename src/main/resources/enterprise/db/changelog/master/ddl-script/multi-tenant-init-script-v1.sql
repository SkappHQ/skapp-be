-- liquibase formatted sql

-- changeset AkilaSachintha:multi-tenant-init-script-v1
CREATE TABLE IF NOT EXISTS tenant
(
    tenant_name VARCHAR(100) PRIMARY KEY,
    is_active   BIT(1) NOT NULL DEFAULT 1,
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP
);