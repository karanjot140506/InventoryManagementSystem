package com.inventory.report;

import com.inventory.manager.InventoryManager;
import com.inventory.model.Product;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Generates inventory/sales reports using MULTITHREADING.
 *
 * Strategy: group products by category, then spawn one worker thread PER
 * CATEGORY to compute stats (total stock value, item count, low-stock
 * items) concurrently. Results are collected via Future objects and merged
 * on the main thread. This demonstrates ExecutorService + Callable + Future.
 */
public class SalesReportGenerator {

    private final InventoryManager inventoryManager;

    public SalesReportGenerator(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    /** Simple holder for one category's computed stats. */
    public static class CategoryReport {
        public final String category;
        public final int itemCount;
        public final int totalStock;
        public final double totalValue;
        public final List<Product> lowStockItems;

        public CategoryReport(String category, int itemCount, int totalStock,
                               double totalValue, List<Product> lowStockItems) {
            this.category = category;
            this.itemCount = itemCount;
            this.totalStock = totalStock;
            this.totalValue = totalValue;
            this.lowStockItems = lowStockItems;
        }
    }

    /**
     * Generates a full report across all categories using a thread pool.
     * Each category is processed by its own task; low-stock threshold is
     * configurable (e.g. 5 units).
     */
    public List<CategoryReport> generateReport(int lowStockThreshold) throws InterruptedException, ExecutionException {
        List<Product> allProducts = inventoryManager.getAllProducts();

        // Group product IDs by category
        Map<String, java.util.List<Product>> byCategory = new ConcurrentHashMap<>();
        for (Product p : allProducts) {
            byCategory.computeIfAbsent(p.getCategory(), k -> new CopyOnWriteArrayList<>()).add(p);
        }

        Set<String> categories = byCategory.keySet();
        int threadCount = Math.max(1, categories.size());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            List<Future<CategoryReport>> futures = new java.util.ArrayList<>();

            for (String category : categories) {
                List<Product> productsInCategory = byCategory.get(category);

                Callable<CategoryReport> task = () -> {
                    int itemCount = productsInCategory.size();
                    int totalStock = 0;
                    double totalValue = 0;
                    List<Product> lowStock = new java.util.ArrayList<>();

                    for (Product p : productsInCategory) {
                        totalStock += p.getQuantity();
                        totalValue += p.getTotalValue();
                        if (p.getQuantity() <= lowStockThreshold) {
                            lowStock.add(p);
                        }
                    }

                    System.out.println("  [Thread: " + Thread.currentThread().getName() +
                            "] processed category '" + category + "'");

                    return new CategoryReport(category, itemCount, totalStock, totalValue, lowStock);
                };

                futures.add(executor.submit(task));
            }

            List<CategoryReport> results = new java.util.ArrayList<>();
            for (Future<CategoryReport> future : futures) {
                results.add(future.get()); // blocks until that category's thread finishes
            }
            return results;

        } finally {
            executor.shutdown();
        }
    }

    /** Pretty-prints the report to console. */
    public void printReport(List<CategoryReport> reports) {
        System.out.println("=========================================================");
        System.out.println("                 SALES / STOCK REPORT");
        System.out.println("=========================================================");

        double grandTotalValue = 0;
        int grandTotalStock = 0;

        for (CategoryReport r : reports) {
            System.out.printf("Category: %-15s | Items: %-4d | Stock: %-6d | Value: Rs.%.2f%n",
                    r.category, r.itemCount, r.totalStock, r.totalValue);

            if (!r.lowStockItems.isEmpty()) {
                System.out.println("   Low stock alert:");
                for (Product p : r.lowStockItems) {
                    System.out.println("     - " + p.getName() + " (only " + p.getQuantity() + " left)");
                }
            }

            grandTotalValue += r.totalValue;
            grandTotalStock += r.totalStock;
        }

        System.out.println("---------------------------------------------------------");
        System.out.printf("GRAND TOTAL STOCK: %d units | GRAND TOTAL VALUE: Rs.%.2f%n",
                grandTotalStock, grandTotalValue);
        System.out.println("=========================================================");
    }
}
