-- Creating the `sales_process` table
CREATE TABLE `sales_process`
(
    `sale_pass_no`          varchar(255) NOT NULL,
    `consignment_weight` double NOT NULL,
    `product_name`          varchar(255) DEFAULT NULL,
    `product_type`          varchar(255) DEFAULT NULL,
    `purchase_process_date` date         DEFAULT NULL,
    `status`                bit(1)       NOT NULL,
    `transporter_name`      varchar(255) DEFAULT NULL,
    `vehicle_no`            varchar(255) DEFAULT NULL,
    `sale_order_no`         varchar(255) DEFAULT NULL,
    PRIMARY KEY (`sale_pass_no`),
    KEY                     `FKc4noaj8jgfky4jd6mt7oj2frk` (`sale_order_no`),
    CONSTRAINT `FKc4noaj8jgfky4jd6mt7oj2frk` FOREIGN KEY (`sale_order_no`) REFERENCES `sales_order` (`sale_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;