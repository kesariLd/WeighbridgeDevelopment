-- V4__Create_gate_entry_transaction_table.sql

-- Table structure for table `gate_entry_transaction`
CREATE TABLE `gate_entry_transaction`
(
    `ticket_no`        int    NOT NULL AUTO_INCREMENT,
    `challan_date`     date         DEFAULT NULL,
    `challan_no`       varchar(255) DEFAULT NULL,
    `company_id`       varchar(255) DEFAULT NULL,
    `customer_id`      bigint NOT NULL,
    `dl_no`            varchar(255) DEFAULT NULL,
    `driver_name`      varchar(255) DEFAULT NULL,
    `ewaybill_no`      varchar(255) DEFAULT NULL,
    `material_id`      bigint NOT NULL,
    `material_type`    varchar(255) DEFAULT NULL,
    `po_no`            varchar(255) DEFAULT NULL,
    `site_id`          varchar(255) DEFAULT NULL,
    `supplier_id`      bigint NOT NULL,
    `supply_consignment_weight` double DEFAULT NULL,
    `tp_no`            varchar(255) DEFAULT NULL,
    `transaction_date` date         DEFAULT NULL,
    `transaction_type` varchar(255) DEFAULT NULL,
    `transporter_id`   bigint NOT NULL,
    `vehicle_id`       bigint NOT NULL,
    `vehicle_in`       datetime(6) DEFAULT NULL,
    `vehicle_out`      datetime(6) DEFAULT NULL,
    PRIMARY KEY (`ticket_no`),
    KEY                `idx_gate_entry_transaction` (`site_id`,`company_id`,`transaction_date` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
