-- Table structure for table `quality_transaction`
CREATE TABLE `quality_transaction`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `quality_range_id` varchar(255) DEFAULT NULL,
    `quality_values`   varchar(255) DEFAULT NULL,
    `ticket_no`        int          DEFAULT NULL,
    `is_quality_good`  bit(1)       DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_bl3hqmhjfropblelfwwavb6oh` (`ticket_no`),
    CONSTRAINT `FKa088l9xceipoy5hbr5yr5lwjn` FOREIGN KEY (`ticket_no`) REFERENCES `gate_entry_transaction` (`ticket_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
