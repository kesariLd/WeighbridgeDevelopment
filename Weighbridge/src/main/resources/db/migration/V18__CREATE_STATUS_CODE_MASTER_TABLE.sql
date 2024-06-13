-- Table structure for table `status_code_master`
CREATE TABLE `status_code_master`
(
    `status_code`        varchar(255) NOT NULL,
    `status_description` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`status_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Insert initial data into `status_code_master`
INSERT INTO `status_code_master` (`status_code`, `status_description`)
VALUES ('ACTIVE', 'Active status'),
       ('INACTIVE', 'Inactive status'),
       ('PENDING', 'Pending status'),
       ('DELETED', 'Deleted status');
