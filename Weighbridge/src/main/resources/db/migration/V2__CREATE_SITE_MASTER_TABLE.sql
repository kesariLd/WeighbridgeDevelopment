-- Table structure for table `site_master`
CREATE TABLE `site_master`
(
    `site_id`            varchar(255) NOT NULL,
    `site_address`       varchar(255) DEFAULT NULL,
    `site_created_by`    varchar(255) DEFAULT NULL,
    `site_created_date`  datetime(6) DEFAULT NULL,
    `site_modified_by`   varchar(255) DEFAULT NULL,
    `site_modified_date` datetime(6) DEFAULT NULL,
    `site_name`          varchar(255) NOT NULL,
    `site_status`        varchar(255) DEFAULT NULL,
    `company_id`         varchar(255) DEFAULT NULL,
    PRIMARY KEY (`site_id`),
    KEY                  `FKfqe1o6ca2rdy2hir11n45batb` (`company_id`),
    CONSTRAINT `FKfqe1o6ca2rdy2hir11n45batb` FOREIGN KEY (`company_id`) REFERENCES `company_master` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table `site_master`
INSERT INTO `site_master`
VALUES ('all', 'ALL', NULL, NULL, NULL, NULL, 'ALL', 'ACTIVE', 'all');
