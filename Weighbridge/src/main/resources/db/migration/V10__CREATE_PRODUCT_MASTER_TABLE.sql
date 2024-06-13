-- Table structure for table `product_master`
CREATE TABLE `product_master`
(
    `product_id`            bigint       NOT NULL AUTO_INCREMENT,
    `product_created_by`    varchar(255) DEFAULT NULL,
    `product_created_date`  datetime(6) DEFAULT NULL,
    `product_modified_by`   varchar(255) DEFAULT NULL,
    `product_modified_date` datetime(6) DEFAULT NULL,
    `product_name`          varchar(255) NOT NULL,
    `product_status`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
