CREATE TABLE `vending_storage`
(
    `id`         int          NOT NULL AUTO_INCREMENT,
    `item_name` varchar(128),
    `price` double,
    `quantity` bigint,
    `version` bigint,
    `create_date` timestamp,
    `update_date` timestamp,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `bank_storage`
(
    `id` int not null auto_increment,
    `type` varchar(128),
    `quantity` bigint,

    PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transaction`
(
    `id`         int          NOT NULL AUTO_INCREMENT,
    `item_name` varchar(128),
    `price` double,
    `paid` bigint,
    `change` bigint,
    `version` bigint,
    `create_date` timestamp,
    `update_date` timestamp,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;