-- liquibase formatted sql

-- changeset AkilaSachintha:common-ddl-script-v1-add-column-user
ALTER TABLE `user`
    ADD COLUMN `client_id` BIGINT NULL;

-- rollback DROP COLUMN `client_id` FROM `user`;
