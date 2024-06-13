-- Table structure for table `material_type_master`
CREATE TABLE `material_type_master`
(
    `material_type_id`   bigint NOT NULL AUTO_INCREMENT,
    `material_type_name` varchar(255) DEFAULT NULL,
    `material_id`        bigint       DEFAULT NULL,
    PRIMARY KEY (`material_type_id`),
    KEY                  `FKq3jma0e1kv65q73mfyubjjq7s` (`material_id`),
    CONSTRAINT `FKq3jma0e1kv65q73mfyubjjq7s` FOREIGN KEY (`material_id`) REFERENCES `material_master` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
