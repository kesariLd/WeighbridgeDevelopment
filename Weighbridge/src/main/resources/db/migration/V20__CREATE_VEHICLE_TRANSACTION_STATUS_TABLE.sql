-- Table structure for table `vehicle_transaction_status`
CREATE TABLE `vehicle_transaction_status`
(
    `ticket_no`   int NOT NULL,
    `status_code` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`ticket_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
