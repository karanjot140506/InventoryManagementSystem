package com.inventory.db;

/**
 * Central place to configure your database connection.
 *
 * Using H2 - an embedded, file-based database. No server install,
 * no password setup required. Data is stored in a local file
 * (inventory_db.mv.db) created automatically in the project folder
 * the first time the app runs.
 */
public class DBConfig {
    // Creates/uses a file named "inventory_db.mv.db" in the folder you run the app from.
    public static final String URL = "jdbc:h2:C:/h2data/inventory_db;AUTO_SERVER=TRUE";
    public static final String USER = "sa";
    public static final String PASSWORD = "";
    public static final String DRIVER = "org.h2.Driver";
}
