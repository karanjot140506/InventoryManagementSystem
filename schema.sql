-- H2 Database schema.
-- Unlike MySQL, H2 does NOT need "CREATE DATABASE" - the database
-- is just the file specified in DBConfig.java (created automatically).
-- Run this script once using H2 Console (see README.md) to create the tables.

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bills (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    bill_date TIMESTAMP NOT NULL,
    grand_total DECIMAL(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS bill_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    bill_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE
);

-- Sample data (optional) - only insert if table is empty, so re-running this
-- script doesn't create duplicates.
INSERT INTO products (name, category, price, quantity)
SELECT * FROM (
    SELECT 'Laptop', 'Electronics', 55000.00, 10
    UNION ALL SELECT 'Wireless Mouse', 'Electronics', 599.00, 50
    UNION ALL SELECT 'Notebook', 'Stationery', 40.00, 200
    UNION ALL SELECT 'Pen', 'Stationery', 10.00, 500
    UNION ALL SELECT 'Office Chair', 'Furniture', 4500.00, 3
) AS sample_data
WHERE NOT EXISTS (SELECT 1 FROM products);
