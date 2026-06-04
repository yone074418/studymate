CREATE DATABASE IF NOT EXISTS `studymate`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `studymate`;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'User ID',
    `username` VARCHAR(50) NOT NULL COMMENT 'Username',
    `email` VARCHAR(100) NOT NULL COMMENT 'Email',
    `password` VARCHAR(255) NOT NULL COMMENT 'Encrypted password',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT 'Nickname',
    `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT 'Avatar URL',
    `target_position` VARCHAR(100) DEFAULT NULL COMMENT 'Target position',
    `daily_target_minutes` INT NOT NULL DEFAULT 120 COMMENT 'Daily target minutes',
    `study_stage` VARCHAR(50) DEFAULT NULL COMMENT 'Study stage',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Account status: 1 enabled, 0 disabled',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logic delete flag: 0 active, 1 deleted',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`),
    UNIQUE KEY `uk_user_email` (`email`),
    KEY `idx_user_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='User table';

CREATE TABLE IF NOT EXISTS `study_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Study category ID',
    `name` VARCHAR(50) NOT NULL COMMENT 'Category name',
    `description` VARCHAR(255) DEFAULT NULL COMMENT 'Category description',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT 'Sort order',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT 'Status: 1 enabled, 0 disabled',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_study_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Study category table';

CREATE TABLE IF NOT EXISTS `study_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Study record ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `record_date` DATE NOT NULL COMMENT 'Study date',
    `raw_content` TEXT NOT NULL COMMENT 'Raw user input',
    `duration_minutes` INT NOT NULL DEFAULT 0 COMMENT 'Study duration in minutes',
    `study_content` TEXT COMMENT 'Study content summary',
    `emotion_status` VARCHAR(30) DEFAULT NULL COMMENT 'Emotion status',
    `tomorrow_plan` TEXT COMMENT 'Tomorrow plan',
    `ai_summary` TEXT COMMENT 'AI summary',
    `ai_comfort` TEXT COMMENT 'AI comfort feedback',
    `ai_result_json` JSON DEFAULT NULL COMMENT 'AI structured result JSON',
    `remark` TEXT COMMENT 'User remark',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logic delete flag: 0 active, 1 deleted',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    KEY `idx_study_record_user_date` (`user_id`, `record_date`),
    KEY `idx_study_record_user_deleted` (`user_id`, `deleted`),
    CONSTRAINT `fk_study_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Study record table';

CREATE TABLE IF NOT EXISTS `study_record_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary ID',
    `study_record_id` BIGINT NOT NULL COMMENT 'Study record ID',
    `category_id` BIGINT NOT NULL COMMENT 'Study category ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_record_category` (`study_record_id`, `category_id`),
    KEY `idx_record_category_user` (`user_id`),
    KEY `idx_record_category_category` (`category_id`),
    CONSTRAINT `fk_record_category_record` FOREIGN KEY (`study_record_id`) REFERENCES `study_record` (`id`),
    CONSTRAINT `fk_record_category_category` FOREIGN KEY (`category_id`) REFERENCES `study_category` (`id`),
    CONSTRAINT `fk_record_category_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Study record category relation table';

CREATE TABLE IF NOT EXISTS `weak_point` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Weak point ID',
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `study_record_id` BIGINT NOT NULL COMMENT 'Source study record ID',
    `category_id` BIGINT DEFAULT NULL COMMENT 'Study category ID',
    `content` VARCHAR(255) NOT NULL COMMENT 'Weak point content',
    `resolved` TINYINT NOT NULL DEFAULT 0 COMMENT 'Resolved flag: 0 unresolved, 1 resolved',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT 'Logic delete flag: 0 active, 1 deleted',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
    PRIMARY KEY (`id`),
    KEY `idx_weak_point_user_time` (`user_id`, `create_time`),
    KEY `idx_weak_point_user_content` (`user_id`, `content`),
    KEY `idx_weak_point_record` (`study_record_id`),
    CONSTRAINT `fk_weak_point_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_weak_point_record` FOREIGN KEY (`study_record_id`) REFERENCES `study_record` (`id`),
    CONSTRAINT `fk_weak_point_category` FOREIGN KEY (`category_id`) REFERENCES `study_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Weak point table';

CREATE TABLE IF NOT EXISTS `ai_call_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'AI call log ID',
    `user_id` BIGINT DEFAULT NULL COMMENT 'User ID',
    `study_record_id` BIGINT DEFAULT NULL COMMENT 'Related study record ID',
    `request_type` VARCHAR(50) NOT NULL COMMENT 'Request type',
    `model_name` VARCHAR(100) DEFAULT NULL COMMENT 'Model name',
    `prompt` TEXT COMMENT 'Prompt',
    `request_content` TEXT COMMENT 'Request content',
    `response_content` MEDIUMTEXT COMMENT 'AI raw response',
    `success` TINYINT NOT NULL DEFAULT 0 COMMENT 'Success flag: 1 success, 0 failed',
    `error_message` TEXT COMMENT 'Error message',
    `duration_ms` INT DEFAULT NULL COMMENT 'Duration in milliseconds',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    PRIMARY KEY (`id`),
    KEY `idx_ai_call_log_user_time` (`user_id`, `create_time`),
    KEY `idx_ai_call_log_record` (`study_record_id`),
    CONSTRAINT `fk_ai_call_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_ai_call_log_record` FOREIGN KEY (`study_record_id`) REFERENCES `study_record` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI call log table';

INSERT INTO `study_category` (`name`, `description`, `sort_order`)
VALUES
    ('Java基础', 'Java syntax, collections, exceptions, IO and concurrency basics', 10),
    ('Spring Boot', 'Spring Boot, Spring MVC and Spring Security framework content', 20),
    ('MySQL', 'SQL, indexes, transactions, locks and MVCC', 30),
    ('Redis', 'Cache, persistence, data structures and distributed locks', 40),
    ('MyBatis Plus', 'MyBatis Plus CRUD, pagination, Wrapper and plugins', 50),
    ('计算机基础', 'Computer networks, operating systems, data structures and algorithms', 60),
    ('项目实战', 'Project features, API design, deployment, integration and troubleshooting', 70),
    ('面试复盘', 'Interview question review, mistake review and expression practice', 80)
ON DUPLICATE KEY UPDATE
    `description` = VALUES(`description`),
    `sort_order` = VALUES(`sort_order`),
    `update_time` = CURRENT_TIMESTAMP;
