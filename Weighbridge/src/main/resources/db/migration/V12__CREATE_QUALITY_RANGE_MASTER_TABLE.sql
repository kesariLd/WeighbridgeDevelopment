-- Table structure for table `quality_range_master`
CREATE TABLE `quality_range_master`
(
    `quality_range_id` bigint NOT NULL AUTO_INCREMENT,
    `parameter_name`   varchar(255) DEFAULT NULL,
    `range_from` double DEFAULT NULL,
    `range_to` double DEFAULT NULL,
    `supplier_address` varchar(255) DEFAULT NULL,
    `supplier_name`    varchar(255) DEFAULT NULL,
    `material_id`      bigint       DEFAULT NULL,
    `product_id`       bigint       DEFAULT NULL,
    PRIMARY KEY (`quality_range_id`),
    KEY                `FK824dgmd2ewklugnew24ivhbyu` (`material_id`),
    KEY                `FK2uiqwrexf76gnpc1hwthp3j6t` (`product_id`),
    CONSTRAINT `FK824dgmd2ewklugnew24ivhbyu` FOREIGN KEY (`material_id`) REFERENCES `material_master` (`material_id`),
    CONSTRAINT `FK2uiqwrexf76gnpc1hwthp3j6t` FOREIGN KEY (`product_id`) REFERENCES `product_master` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
