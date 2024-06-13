-- Table structure for table `sales_order`
CREATE TABLE `sales_order`
(
    `sale_order_no`         varchar(255) NOT NULL,
    `balance_quantity` double NOT NULL,
    `broker_address`        varchar(255) DEFAULT NULL,
    `broker_name`           varchar(255) DEFAULT NULL,
    `customer_id`           bigint       NOT NULL,
    `ordered_quantity` double NOT NULL,
    `product_name`          varchar(255) NOT NULL,
    `progressive_quantity` double NOT NULL,
    `purchase_order_no`     varchar(255) NOT NULL,
    `purchase_ordered_date` date         NOT NULL,
    `status`                bit(1)       NOT NULL,
    `company_id`            varchar(255) NOT NULL,
    `site_id`               varchar(255) NOT NULL,
    PRIMARY KEY (`sale_order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;