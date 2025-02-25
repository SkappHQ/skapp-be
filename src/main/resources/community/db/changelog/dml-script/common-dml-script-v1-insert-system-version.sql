-- liquibase formatted sql

-- changeset AkilaSilva:common-dml-script-v1-insert-system-version
INSERT INTO `system_version` (`version`, `reason`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`)
SELECT '1.0.0', 'INITIAL_VERSION', NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM `system_version`);

-- rollback DELETE FROM `system_version` WHERE `version` = '1.0.0';
