-- Table structure for table `user_roles`
CREATE TABLE `user_roles`
(
    `user_id` varchar(255) NOT NULL,
    `role_id` int          NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    KEY       `FK2ptgru2sianmji5yk32p4kdfj` (`role_id`),
    CONSTRAINT `FK2ptgru2sianmji5yk32p4kdfj` FOREIGN KEY (`role_id`) REFERENCES `role_master` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table `user_roles`
INSERT INTO `user_roles`
VALUES ('admin', 1);
