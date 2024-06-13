-- Table structure for table `material_master`
CREATE TABLE `material_master`
(
    `material_id`            bigint       NOT NULL AUTO_INCREMENT,
    `material_created_by`    varchar(255) DEFAULT NULL,
    `material_created_date`  datetime(6) DEFAULT NULL,
    `material_modified_by`   varchar(255) DEFAULT NULL,
    `material_modified_date` datetime(6) DEFAULT NULL,
    `material_name`          varchar(255) NOT NULL,
    `material_status`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

