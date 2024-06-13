-- Table structure for table `user_master`
CREATE TABLE `user_master`
(
    `user_id`            varchar(15) NOT NULL,
    `user_contact_no`    varchar(255) DEFAULT NULL,
    `user_created_by`    varchar(255) DEFAULT NULL,
    `user_created_date`  datetime(6) DEFAULT NULL,
    `user_email_id`      varchar(255) DEFAULT NULL,
    `user_first_name`    varchar(50) NOT NULL,
    `user_last_name`     varchar(50) NOT NULL,
    `user_middle_name`   varchar(255) DEFAULT NULL,
    `user_modified_by`   varchar(255) DEFAULT NULL,
    `user_modified_date` datetime(6) DEFAULT NULL,
    `user_status`        varchar(255) DEFAULT NULL,
    `company_id`         varchar(255) DEFAULT NULL,
    `site_id`            varchar(255) DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `UK_1mtgjo1ps2njx18m3lj8s1hoh` (`user_email_id`),
    KEY                  `FKgijux393gqxtkb1ct3ksiqlf5` (`company_id`),
    KEY                  `FKs7948pt00opcqkyrlw8mrtlqs` (`site_id`),
    CONSTRAINT `FKgijux393gqxtkb1ct3ksiqlf5` FOREIGN KEY (`company_id`) REFERENCES `company_master` (`company_id`),
    CONSTRAINT `FKs7948pt00opcqkyrlw8mrtlqs` FOREIGN KEY (`site_id`) REFERENCES `site_master` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table `user_master`
INSERT INTO `user_master`
VALUES ('admin', NULL, NULL, NULL, NULL, 'Admin', 'User', '', NULL, NULL, 'ACTIVE', 'all', 'all');
