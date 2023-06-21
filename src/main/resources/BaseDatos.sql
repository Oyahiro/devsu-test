CREATE TABLE `person` (
                          `id` VARCHAR(36) NOT NULL,
                          `name` VARCHAR(100) NOT NULL,
                          `gender` VARCHAR(10) NOT NULL,
                          `age` INT NOT NULL,
                          `identification` VARCHAR(100) NOT NULL,
                          `address` VARCHAR(255) NOT NULL,
                          `phone` VARCHAR(20) NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `client` (
                          `id` VARCHAR(36) NOT NULL,
                          `client_id` VARCHAR(100) NOT NULL,
                          `password` VARCHAR(100) NOT NULL,
                          `status` VARCHAR(20) NOT NULL,
                          PRIMARY KEY (`id`),
                          FOREIGN KEY (`id`) REFERENCES `person`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `account` (
                           `id` VARCHAR(36) NOT NULL,
                           `account_number` VARCHAR(20) NOT NULL,
                           `account_type` VARCHAR(20) NOT NULL,
                           `initial_balance` DOUBLE NOT NULL,
                           `status` VARCHAR(20) NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `movement` (
                            `id` VARCHAR(36) NOT NULL,
                            `date` DATETIME NOT NULL,
                            `movement_type` VARCHAR(20) NOT NULL,
                            `value` DOUBLE NOT NULL,
                            `balance` DOUBLE NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
