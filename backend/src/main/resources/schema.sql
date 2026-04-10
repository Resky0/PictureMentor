-- 创建数据库
CREATE DATABASE IF NOT EXISTS photo_mentor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE photo_mentor;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    avatar VARCHAR(255),
    email VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 照片表
CREATE TABLE IF NOT EXISTS photo (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    url VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 评分表
CREATE TABLE IF NOT EXISTS score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    photo_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    total_score INT NOT NULL,
    composition_score INT NOT NULL,
    lighting_score INT NOT NULL,
    color_score INT NOT NULL,
    focus_score INT NOT NULL,
    suggestions TEXT,
    analysis TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_photo_id (photo_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO user (username, email) VALUES 
('demo_user', 'demo@example.com');
