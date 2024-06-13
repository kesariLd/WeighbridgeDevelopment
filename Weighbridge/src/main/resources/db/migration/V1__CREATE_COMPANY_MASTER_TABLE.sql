-- Table structure for table `company_master`
CREATE TABLE `company_master`
(
    `company_id`            varchar(255) NOT NULL,
    `company_address`       varchar(255) DEFAULT NULL,
    `company_contact_no`    varchar(255) DEFAULT NULL,
    `company_created_by`    varchar(255) DEFAULT NULL,
    `company_created_date`  datetime(6) DEFAULT NULL,
    `company_email`         varchar(255) DEFAULT NULL,
    `company_modified_by`   varchar(255) DEFAULT NULL,
    `company_modified_date` datetime(6) DEFAULT NULL,
    `company_name`          varchar(255) NOT NULL,
    `company_status`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table `company_master`
INSERT INTO `company_master`
VALUES ('all', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'ALL', 'ACTIVE');
