-- Table structure for table `user_history`
CREATE TABLE `user_history`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT,
    `company`            varchar(255) DEFAULT NULL,
    `roles`              varchar(255) DEFAULT NULL,
    `site`               varchar(255) DEFAULT NULL,
    `user_created_by`    varchar(255) DEFAULT NULL,
    `user_created_date`  datetime(6) DEFAULT NULL,
    `user_id`            varchar(255) DEFAULT NULL,
    `user_modified_by`   varchar(255) DEFAULT NULL,
    `user_modified_date` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

