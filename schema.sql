-- Create database
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50),
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Sample products will be inserted by DatabaseInitializer if not exists

-- Insert sample products
INSERT IGNORE INTO products (name, description, price, category, stock) VALUES
('Laptop Pro 15', 'High-performance laptop with 15-inch display, 16GB RAM, 512GB SSD', 1299.99, 'Electronics', 50),
('Wireless Headphones', 'Noise-canceling wireless headphones with 30-hour battery life', 199.99, 'Electronics', 100),
('Running Shoes', 'Lightweight running shoes with responsive cushioning', 89.99, 'Sports', 200),
('Coffee Maker', 'Automatic drip coffee maker with programmable timer', 59.99, 'Home', 75),
('Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 129.99, 'Electronics', 60),
('Yoga Mat', 'Non-slip yoga mat with carrying strap', 29.99, 'Sports', 150);

-- Insert sample reviews
INSERT IGNORE INTO reviews (product_id, user_id, rating, comment) VALUES
(1, 1, 5, 'Excellent laptop! Fast and reliable.'),
(2, 1, 4, 'Great sound quality, comfortable fit.'),
(3, 1, 5, 'Best running shoes I have ever owned.');
