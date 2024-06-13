-- Table structure for table `role_master`
CREATE TABLE `role_master`
(
    `role_id`            int          NOT NULL AUTO_INCREMENT,
    `role_created_by`    varchar(255) DEFAULT NULL,
    `role_created_date`  datetime(6)  DEFAULT NULL,
    `role_modified_by`   varchar(255) DEFAULT NULL,
    `role_modified_date` datetime(6)  DEFAULT NULL,
    `role_name`          varchar(255) NOT NULL,
    `role_status`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`role_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

-- Dumping data for table `role_master`
INSERT INTO `role_master`
VALUES (1, NULL, NULL, NULL, NULL, 'ADMIN', 'ACTIVE');
