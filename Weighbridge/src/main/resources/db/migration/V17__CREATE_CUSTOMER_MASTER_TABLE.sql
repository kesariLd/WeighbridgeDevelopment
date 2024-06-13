-- Table structure for table `customer_master`
CREATE TABLE `customer_master`
(
    `customer_id`            bigint       NOT NULL AUTO_INCREMENT,
    `city`                   varchar(255) DEFAULT NULL,
    `country`                varchar(255) DEFAULT NULL,
    `customer_address_line1` varchar(255) NOT NULL,
    `customer_address_line2` varchar(255) DEFAULT NULL,
    `customer_contact_no`    varchar(255) DEFAULT NULL,
    `customer_created_by`    varchar(255) DEFAULT NULL,
    `customer_created_date`  datetime(6) DEFAULT NULL,
    `customer_email`         varchar(255) DEFAULT NULL,
    `customer_modified_by`   varchar(255) DEFAULT NULL,
    `customer_modified_date` datetime(6) DEFAULT NULL,
    `customer_name`          varchar(255) NOT NULL,
    `customer_status`        varchar(255) DEFAULT NULL,
    `state`                  varchar(255) DEFAULT NULL,
    `zip`                    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`customer_id`),
    UNIQUE KEY `UK_3tthjqiod5oppfa0lkognddny` (`customer_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
