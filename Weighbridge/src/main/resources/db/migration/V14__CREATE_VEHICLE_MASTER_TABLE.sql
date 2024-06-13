-- Table structure for table `vehicle_master`
CREATE TABLE `vehicle_master`
(
    `id`                    bigint       NOT NULL AUTO_INCREMENT,
    `vehicle_created_by`    varchar(255) DEFAULT NULL,
    `vehicle_created_date`  datetime(6) DEFAULT NULL,
    `vehicle_fitness_up_to` date         DEFAULT NULL,
    `vehicle_load_capacity` double DEFAULT NULL,
    `vehicle_manufacturer`  varchar(255) DEFAULT NULL,
    `vehicle_modified_by`   varchar(255) DEFAULT NULL,
    `vehicle_modified_date` datetime(6) DEFAULT NULL,
    `vehicle_no`            varchar(255) NOT NULL,
    `vehicle_status`        varchar(255) DEFAULT NULL,
    `vehicle_tare_weight` double DEFAULT NULL,
    `vehicle_type`          varchar(255) DEFAULT NULL,
    `vehicle_wheels_no`     int          DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_ll8x9tlece06o4aod7jqeph08` (`vehicle_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
