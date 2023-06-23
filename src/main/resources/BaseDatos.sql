CREATE DATABASE IF NOT EXISTS devsu;

USE devsu;

DROP TABLE IF EXISTS `movement`;
DROP TABLE IF EXISTS `account`;
DROP TABLE IF EXISTS `authorities`;
DROP TABLE IF EXISTS `client`;
DROP TABLE IF EXISTS `person`;

CREATE TABLE `person` (
                          `id` VARCHAR(36) NOT NULL,
                          `name` VARCHAR(100) NOT NULL,
                          `gender` VARCHAR(20) NOT NULL,
                          `age` INT NOT NULL,
                          `identification_number` VARCHAR(10) NOT NULL,
                          `address` VARCHAR(255) NOT NULL,
                          `phone_number` VARCHAR(10) NOT NULL,
                          PRIMARY KEY (`id`),
                          CONSTRAINT `UK_PERSON_IDENTIFICATION_NUMBER` UNIQUE (`identification_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `client` (
                          `id` VARCHAR(36) NOT NULL,
                          `password` VARCHAR(255) NOT NULL,
                          `status` VARCHAR(10) NOT NULL,
                          `person_id` VARCHAR(36) NOT NULL,
                          PRIMARY KEY (`id`),
                          FOREIGN KEY (`person_id`) REFERENCES `person`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `authorities` (
                               `client_id` VARCHAR(36) NOT NULL,
                               `role` VARCHAR(45) NOT NULL,
                               FOREIGN KEY (`client_id`) REFERENCES `client`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `account` (
                           `id` VARCHAR(36) NOT NULL,
                           `account_number` VARCHAR(10) NOT NULL,
                           `account_type` VARCHAR(10) NOT NULL,
                           `initial_balance` DOUBLE NOT NULL,
                           `status` VARCHAR(10) NOT NULL,
                           `client_id` VARCHAR(36) NOT NULL,
                           PRIMARY KEY (`id`),
                           FOREIGN KEY (`client_id`) REFERENCES `client`(`id`),
                           CONSTRAINT `UK_ACCOUNT_NUMBER` UNIQUE (`account_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `movement` (
                            `id` VARCHAR(36) NOT NULL,
                            `date` DATETIME NOT NULL,
                            `movement_type` VARCHAR(15) NOT NULL,
                            `value` DOUBLE NOT NULL,
                            `balance` DOUBLE NOT NULL,
                            `account_id` VARCHAR(36) NOT NULL,
                            `updated` BOOLEAN NOT NULL,
                            PRIMARY KEY (`id`),
                            FOREIGN KEY (`account_id`) REFERENCES `account`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `person` VALUES ('8901a458-89f2-4966-976d-83eb7c72726c','Christian Chunga','MALE',24,'0941106445','Juan Montalvo','0963179218');
INSERT INTO `client` VALUES ('f8ddb5a9-3af9-43c6-a64e-43812fc10b03','$2a$10$NSGnVMBgY8A4gippgW57g.5XCon9R.JPWwOH3xPyBWA.SIbkfNZQS','ACTIVE','8901a458-89f2-4966-976d-83eb7c72726c');
INSERT INTO `authorities` VALUES ('f8ddb5a9-3af9-43c6-a64e-43812fc10b03','ROLE_ADMIN');
