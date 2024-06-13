-- Table structure for table `product_type_master`
CREATE TABLE `product_type_master`
(
    `product_type_id`   bigint NOT NULL AUTO_INCREMENT,
    `product_type_name` varchar(255) DEFAULT NULL,
    `product_id`        bigint       DEFAULT NULL,
    PRIMARY KEY (`product_type_id`),
    KEY                 `FKpkih3dk2xytrwy61wyln0sdp1` (`product_id`),
    CONSTRAINT `FKpkih3dk2xytrwy61wyln0sdp1` FOREIGN KEY (`product_id`) REFERENCES `product_master` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

