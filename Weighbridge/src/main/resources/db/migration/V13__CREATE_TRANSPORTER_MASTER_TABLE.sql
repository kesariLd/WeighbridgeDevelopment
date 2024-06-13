-- Table structure for table `transporter_master`
CREATE TABLE `transporter_master`
(
    `id`                        bigint       NOT NULL AUTO_INCREMENT,
    `status`                    varchar(255) DEFAULT NULL,
    `transporter_address`       varchar(255) DEFAULT NULL,
    `transporter_contact_no`    varchar(255) DEFAULT NULL,
    `transporter_created_by`    varchar(255) DEFAULT NULL,
    `transporter_created_date`  datetime(6) DEFAULT NULL,
    `transporter_email_id`      varchar(255) DEFAULT NULL,
    `transporter_modified_by`   varchar(255) DEFAULT NULL,
    `transporter_modified_date` datetime(6) DEFAULT NULL,
    `transporter_name`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `transporter_master`
    ADD UNIQUE KEY `UK_transporter_name` (`transporter_name`);
