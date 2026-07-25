package com.inventory.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides JDBC Connection objects to the rest of the application.
 * Uses simple singleton-style access; each call returns a fresh connection
 * (safe for use across multiple threads since each thread gets its own Connection).
 */
public class DBConnection {

    static {
        try {
            Class.forName(DBConfig.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found in classpath!");
            System.err.println("Make sure lib/h2-2.2.224.jar (included in this project) is on your classpath.");
            System.err.println("(see README.md).");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
    }
}
