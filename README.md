# Inventory Management System

A **console-based Java application** for managing product inventory, stock levels, billing, and sales reports. Data is stored in an embedded **H2** database and accessed through a simple text menu in the terminal.

---

## Features

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
│   ├── Main.java                 # Application entry point & menu loop
│   ├── billing/
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
mkdir -p bin
find src -name "*.java" > sources.txt
javac -cp "lib/*" -d bin @sources.txt
```

### 3. Run

**Windows:**

```powershell
java -cp "bin;lib/*" com.inventory.Main
```

**macOS / Linux:**

```bash
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

---

## Notes

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
