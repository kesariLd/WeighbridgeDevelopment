-- Table structure for table `user_authentication`
CREATE TABLE `user_authentication`
(
    `id`              bigint NOT NULL AUTO_INCREMENT,
    `defaultpassword` varchar(255) DEFAULT NULL,
    `otp`             varchar(255) DEFAULT NULL,
    `user_id`         varchar(15)  DEFAULT NULL,
    `password`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_fr1f2tttqooe3goomjhebhb5a` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table `user_authentication`
INSERT INTO `user_authentication`
VALUES (1, NULL, NULL, 'admin', '$2a$12$yxdjPSbaldSe9G0qrpSCw.FaUoUroSHvpfk7KEbzgXADFNENIGyZe');
