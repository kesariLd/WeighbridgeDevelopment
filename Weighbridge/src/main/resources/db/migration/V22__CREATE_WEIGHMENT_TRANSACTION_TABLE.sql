-- Table structure for table `weighment_transaction`
CREATE TABLE `weighment_transaction`
(
    `weighment_no` int NOT NULL AUTO_INCREMENT,
    `gross_weight` double NOT NULL,
    `machine_id`   varchar(255) DEFAULT NULL,
    `net_weight` double NOT NULL,
    `tare_weight` double NOT NULL,
    `temporary_weight` double NOT NULL,
    `ticket_no`    int          DEFAULT NULL,
    PRIMARY KEY (`weighment_no`),
    UNIQUE KEY `UK_63v4fcwahr72jtg5q8wwjkass` (`ticket_no`),
    CONSTRAINT `FKksurplpa3y4v6mv9bigeqn3wj` FOREIGN KEY (`ticket_no`) REFERENCES `gate_entry_transaction` (`ticket_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

