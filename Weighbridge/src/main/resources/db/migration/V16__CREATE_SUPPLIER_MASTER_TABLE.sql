-- Table structure for table `supplier_master`
CREATE TABLE `supplier_master`
(
    `supplier_id`            bigint       NOT NULL AUTO_INCREMENT,
    `city`                   varchar(255) DEFAULT NULL,
    `country`                varchar(255) DEFAULT NULL,
    `state`                  varchar(255) DEFAULT NULL,
    `supplier_address_line1` varchar(255) NOT NULL,
    `supplier_address_line2` varchar(255) DEFAULT NULL,
    `supplier_contact_no`    varchar(255) DEFAULT NULL,
    `supplier_created_by`    varchar(255) DEFAULT NULL,
    `supplier_created_date`  datetime(6) DEFAULT NULL,
    `supplier_email_id`      varchar(255) DEFAULT NULL,
    `supplier_modified_by`   varchar(255) DEFAULT NULL,
    `supplier_modified_date` datetime(6) DEFAULT NULL,
    `supplier_name`          varchar(255) NOT NULL,
    `supplier_status`        varchar(255) DEFAULT NULL,
    `zip`                    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`supplier_id`),
    UNIQUE KEY `UK_oyrojv08fny6g2rpgivamc7l5` (`supplier_contact_no`),
    UNIQUE KEY `UK_e9ms9ry9plt57x4xymhu1p0r1` (`supplier_email_id`),
    KEY                      `idx_supplier_master1` (`supplier_id`),
    KEY                      `idx_supplier_master2` (`supplier_id`,`supplier_name`,`supplier_address_line1`,`supplier_address_line2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
