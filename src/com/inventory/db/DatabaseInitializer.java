package com.inventory.db;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        String productsTable = """
                CREATE TABLE IF NOT EXISTS products(
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    category VARCHAR(100),
                    price DOUBLE,
                    quantity INT
                );
                """;


        String billsTable = """
                CREATE TABLE IF NOT EXISTS bills(
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    customer_name VARCHAR(100),
                    total_amount DOUBLE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );
                """;


        String billItemsTable = """
                CREATE TABLE IF NOT EXISTS bill_items(
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    bill_id INT,
                    product_id INT,
                    quantity INT,
                    price DOUBLE
                );
                """;


        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement()) {


            stmt.execute(productsTable);
            stmt.execute(billsTable);
            stmt.execute(billItemsTable);


            System.out.println("Database initialized successfully");


        } catch (Exception e) {

            System.out.println("Database initialization failed");
            e.printStackTrace();

        }
    }
}