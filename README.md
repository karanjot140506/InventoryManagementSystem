# Inventory Management System (Java + JDBC + Multithreading + Collections)

A console-based Inventory Management System built with core Java.
A **console-based Java application** for managing product inventory, stock levels, billing, and sales reports. Data is stored in an embedded **H2** database and accessed through a simple text menu in the terminal.

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
---

## Features
1. Add products
2. Update stock
3. Generate bills (deducts stock, saves invoice to DB, prints receipt)
4. Search products (by name/category)
5. Sales/stock report (computed using multiple threads, one per category)
6. List all products

| # | Feature | Description |
|---|---------|-------------|
| 1 | **Add Product** | Register a new product with name, category, price, and initial quantity |
| 2 | **Update Stock** | Increase or adjust stock quantity for an existing product |
| 3 | **Generate Bill** | Create a bill, validate stock, deduct quantity, and save the bill |
| 4 | **Search Products** | Find products by ID or other search criteria |
| 5 | **Sales / Stock Report** | Generate sales and stock reports using **multithreading** |
| 6 | **List All Products** | Display the full product catalog with price, quantity, and value |
| 0 | **Exit** | Close the application |

---

## Tech Stack

- **Language:** Java
- **Interface:** Console / CLI (`System.in` / `System.out`)
- **Database:** H2 (embedded)
- **Architecture:** Layered packages (`model`, `db`, `manager`, `billing`, `report`)

---

## Project Structure

```
InventoryManagementSystem/
├── lib/                          # External libraries (e.g. H2 JDBC driver)
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
│   ├── Main.java                 # Application entry point & menu loop
│   ├── billing/
│   │   └── BillGenerator.java
│   └── report/
│       └── SalesReportGenerator.java # Multithreaded report engine
├── lib/
│   └── h2-2.2.224.jar                # H2 database driver (already included!)
├── schema.sql                        # Run this once to create tables
│   │   └── BillGenerator.java    # Bill creation, stock validation & persistence
│   ├── db/                       # Database access & initialization
│   ├── manager/
│   │   └── InventoryManager.java # Product & stock operations
│   ├── model/                    # Product, Bill, BillItem, etc.
│   └── report/                   # Multithreaded sales/stock reports
├── schema.sql                    # Database schema
├── inventory_db.mv.db            # H2 database file (created/used at runtime)
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
---

## Prerequisites

- **JDK 11+** (JDK 17 recommended)
- Terminal / command prompt, **or** VS Code / IntelliJ IDEA

Check your setup:

```bash
java -version
javac -version
```

---

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>/InventoryManagementSystem
```

> Open the folder that contains `src`, `lib`, and `Main.java`.

### 2. Compile

**Windows (PowerShell):**

```powershell
mkdir bin -Force
javac -cp "lib/*" -d bin (Get-ChildItem -Recurse src\*.java).FullName
```

**macOS / Linux:**

```bash
javac -d bin -cp lib/h2-2.2.224.jar $(find src -name "*.java")
mkdir -p bin
find src -name "*.java" > sources.txt
javac -cp "lib/*" -d bin @sources.txt
```

### 3. Run

**Windows:**

```powershell
java -cp "bin;lib/*" com.inventory.Main
```
(On Windows CMD, use IntelliJ/Eclipse instead — see below, it's easier.)

### 4. Run
**macOS / Linux:**

```bash
java -cp "bin;lib/h2-2.2.224.jar" com.inventory.Main
java -cp "bin:lib/*" com.inventory.Main
```

> If your H2 jar has a specific name (e.g. `h2-2.2.224.jar`), you can also use:
>
> ```powershell
> java -cp "bin;.\lib\h2-2.2.224.jar" com.inventory.Main
> ```

### Run in VS Code

1. Install **Extension Pack for Java** (Microsoft).
2. **File → Open Folder** → select the project root (`src` + `lib`).
3. Open `src/com/inventory/Main.java`.
4. Click **Run** above `main`, or use the terminal commands above.

Optional `.vscode/settings.json`:

```json
{
  "java.project.sourcePaths": ["src"],
  "java.project.outputPath": "bin",
  "java.project.referencedLibraries": ["lib/**/*.jar"]
}
```

---

## Usage

After starting the app, use the menu:

```
========== INVENTORY MANAGEMENT SYSTEM ==========
1. Add Product
2. Update Stock
3. Generate Bill
4. Search Products
5. Sales / Stock Report (multithreaded)
6. List All Products
0. Exit
Enter choice:
```

1. Enter a number (`0`–`6`) and press **Enter**.
2. Follow the prompts (product name, ID, quantity, etc.).
3. Results and confirmations print in the same terminal.
4. Choose **0** to exit.

### Typical workflow

1. **Add Product** — create items (e.g. IPHONE, category MOBILE, price, qty).
2. **Update Stock** — restock when inventory is low.
3. **Generate Bill** — sell items; stock is validated and reduced.
4. **Search / List** — look up products or view the full catalog.
5. **Sales / Stock Report** — review sales and stock (runs with multithreading).

---

## Database

- Uses an **embedded H2** database.
- Schema is defined in `schema.sql` and initialized on startup (via `DatabaseInitializer`).
- Database files (e.g. `inventory_db.mv.db`) are created/updated in the project working directory.
- Run the app from the project root so relative DB paths resolve correctly.

---

## Sample Output

```
========== INVENTORY MANAGEMENT SYSTEM ==========
1. Add Product
2. Update Stock
3. Generate Bill
4. Search Products
5. Sales / Stock Report (multithreaded)
6. List All Products
0. Exit
Enter choice: 6
1    IPHONE    MOBILE    Rs.20000.00    Qty:10    Value: Rs.200000.00
```

Billing example:

```
========================================
GRAND TOTAL: Rs.20000.00
========================================
Bill saved ID: 1001
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

---

## Notes
- Your data persists between runs — it's saved in `inventory_db.mv.db`,
  created next to wherever you run the app from. Delete that file to start fresh.
- All stock-changing operations (`addProduct`, `updateStock`, `reduceStock`) are `synchronized` on
  `InventoryManager` to keep the in-memory cache consistent under concurrent access.
- Want to switch to MySQL/SQL Server later? Only `DBConfig.java` needs to change
  (URL, driver class, credentials) — the DAO/JDBC code stays identical since it's
  all standard `java.sql` API.

- This is a **console-only** project (no GUI / web UI).
- Currency display uses **Rs.** (rupees) in output.
- Option **5** generates reports using **multithreaded** processing.
- Keep the H2 jar inside `lib/` and include it on the classpath when compiling and running.
- Do not commit large/local lock files if you prefer a clean repo; you may add `*.lock.db` / local DB files to `.gitignore` as needed.

---

## Author

**Karanjot Singh**

---

## License

This project is available for educational and personal use. Add a license file (e.g. MIT) if you want to define usage terms for others.
