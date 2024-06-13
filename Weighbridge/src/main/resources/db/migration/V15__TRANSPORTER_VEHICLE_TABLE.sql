-- Table structure for table `transporter_vehicle`
CREATE TABLE `transporter_vehicle`
(
    `transporter_id` bigint NOT NULL,
    `vehicle_id`     bigint NOT NULL,
    PRIMARY KEY (`transporter_id`, `vehicle_id`),
    CONSTRAINT `FK_vehicle_id` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle_master` (`id`),
    CONSTRAINT `FK_transporter_id` FOREIGN KEY (`transporter_id`) REFERENCES `transporter_master` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;