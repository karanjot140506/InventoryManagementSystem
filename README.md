# Inventory Management System (Java + JDBC + Multithreading + Collections)

A console-based Inventory Management System built with core Java.

## Concepts Used
- **Collections**: `ConcurrentHashMap` for thread-safe in-memory product cache, `List`, `CopyOnWriteArrayList`
- **JDBC**: H2 embedded database for products, bills, and bill items (`PreparedStatement`, transactions)
- **Multithreading**: `ExecutorService` + `Callable` + `Future` to generate category-wise sales reports in parallel

## Why H2 instead of MySQL?
H2 is a lightweight, embedded, file-based database that is **100% JDBC** (same
`Connection`/`PreparedStatement`/`ResultSet` code you'd write for MySQL) but needs
**zero installation, no server, and no password setup**. The database is just a
file (`inventory_db.mv.db`) that gets created automatically the first time you
run the app. This makes the project trivially easy to run on any machine.

## Features
1. Add products
2. Update stock
3. Generate bills (deducts stock, saves invoice to DB, prints receipt)
4. Search products (by name/category)
5. Sales/stock report (computed using multiple threads, one per category)
6. List all products

## Project Structure
```
InventoryManagementSystem/
├── src/com/inventory/
│   ├── Main.java                     # Console menu / entry point
│   ├── model/
│   │   ├── Product.java
│   │   ├── Bill.java
│   │   └── BillItem.java
│   ├── db/
│   │   ├── DBConfig.java             # DB connection settings (H2 - no edits needed)
│   │   ├── DBConnection.java
│   │   ├── ProductDAO.java
│   │   └── BillDAO.java
│   ├── manager/
│   │   └── InventoryManager.java     # Collections-based in-memory store
│   ├── billing/
│   │   └── BillGenerator.java
│   └── report/
│       └── SalesReportGenerator.java # Multithreaded report engine
├── lib/
│   └── h2-2.2.224.jar                # H2 database driver (already included!)
├── schema.sql                        # Run this once to create tables
└── README.md
```

## Setup Instructions (Super Simple - No DB Server Needed!)

### 1. Everything is already included
The H2 driver jar is already in the `lib/` folder. No download needed, no
password to set, no service to start.

### 2. Create the tables (one-time step)
You have two options:

**Option A - Let the app create tables automatically (easiest):**
Open `src/com/inventory/Main.java` and nothing needs to change — but you do
need the tables to exist first. Use Option B below once, then you're set forever.

**Option B - Run schema.sql using the H2 Console:**
1. Open a terminal in the project folder.
2. Run the H2 Console (bundled inside the jar):
   ```bash
   java -cp lib/h2-2.2.224.jar org.h2.tools.Console
   ```
   This opens a browser window with a database login screen.
3. In the login screen, set:
   - **JDBC URL**: `jdbc:h2:~/InventoryManagementSystem/inventory_db;AUTO_SERVER=TRUE`
     (or just `jdbc:h2:./inventory_db;AUTO_SERVER=TRUE` if you run the console
     from inside the project folder — this MUST match `DBConfig.java`)
   - **User Name**: `sa`
   - **Password**: (leave blank)
4. Click **Connect**.
5. In the SQL editor box, open/paste the contents of `schema.sql` and click **Run**.
6. This creates `products`, `bills`, `bill_items` tables + 5 sample products.
7. Close the H2 Console browser tab when done.

### 3. Compile
From the project root:
```bash
javac -d bin -cp lib/h2-2.2.224.jar $(find src -name "*.java")
```
(On Windows CMD, use IntelliJ/Eclipse instead — see below, it's easier.)

### 4. Run
```bash
java -cp "bin;lib/h2-2.2.224.jar" com.inventory.Main
```
(On Mac/Linux, use `:` instead of `;` in the classpath: `"bin:lib/h2-2.2.224.jar"`)

## IDE Setup (Recommended — easiest way)
1. Open the `InventoryManagementSystem` folder as a project in **IntelliJ IDEA** or **Eclipse**.
2. Add `lib/h2-2.2.224.jar` to **Project Structure → Libraries** (IntelliJ)
   or **Build Path → Add External JARs** (Eclipse).
3. Run `Main.java` directly. That's it — no database server to start!

## How Multithreading Works Here
When you choose "Sales/Stock Report":
- Products are grouped by category.
- An `ExecutorService` thread pool is created with one thread per category.
- Each thread independently computes: item count, total stock, total value, and low-stock alerts for its category.
- The main thread collects all results via `Future.get()` (this blocks until each thread finishes) and merges them into the final report.
- Console output shows which thread processed which category, so you can see the parallelism happening.

## Notes
- Your data persists between runs — it's saved in `inventory_db.mv.db`,
  created next to wherever you run the app from. Delete that file to start fresh.
- All stock-changing operations (`addProduct`, `updateStock`, `reduceStock`) are `synchronized` on
  `InventoryManager` to keep the in-memory cache consistent under concurrent access.
- Want to switch to MySQL/SQL Server later? Only `DBConfig.java` needs to change
  (URL, driver class, credentials) — the DAO/JDBC code stays identical since it's
  all standard `java.sql` API.
