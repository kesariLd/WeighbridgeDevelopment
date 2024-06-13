-- Table structure for table `transaction_log`
CREATE TABLE `transaction_log`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `status_code` varchar(255) DEFAULT NULL,
    `ticket_no`   int          DEFAULT NULL,
    `timestamp`   datetime(6) DEFAULT NULL,
    `user_id`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY           `idx_transaction_log` (`ticket_no`,`status_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
