package com.inventory.manager;

import com.inventory.db.ProductDAO;
import com.inventory.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory inventory store backed by a ConcurrentHashMap (Collections).
 * Acts as a fast cache layer in front of the database (ProductDAO), so
 * searches/reports don't have to hit the DB every single time, while
 * writes are still persisted via JDBC.
 *
 * ConcurrentHashMap + AtomicInteger make this safe to use from multiple
 * threads at once (e.g. billing thread + report thread running together).
 */
public class InventoryManager {

    private final Map<Integer, Product> productMap = new ConcurrentHashMap<>();
    private final ProductDAO productDAO = new ProductDAO();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    /** Load all products from DB into the in-memory map. Call once at startup. */
    public void loadFromDatabase() {
        List<Product> products = productDAO.getAllProducts();
        for (Product p : products) {
            productMap.put(p.getId(), p);
            idGenerator.updateAndGet(current -> Math.max(current, p.getId()));
        }
        System.out.println("[InventoryManager] Loaded " + products.size() + " products from database.");
    }

    /** Add a new product: persists to DB then caches in memory. */
    public synchronized Product addProduct(String name, String category, double price, int quantity) {
        Product p = new Product(0, name, category, price, quantity);
        int generatedId = productDAO.insertProduct(p);
        if (generatedId == -1) {
            System.out.println("[InventoryManager] Failed to save product to DB.");
            return null;
        }
        p.setId(generatedId);
        productMap.put(generatedId, p);
        return p;
    }

    /** Update stock quantity of an existing product (adds delta, can be negative). */
    public synchronized boolean updateStock(int productId, int newQuantity) {
        Product p = productMap.get(productId);
        if (p == null) {
            System.out.println("[InventoryManager] Product ID " + productId + " not found.");
            return false;
        }
        boolean dbSuccess = productDAO.updateStock(productId, newQuantity);
        if (dbSuccess) {
            p.setQuantity(newQuantity);
        }
        return dbSuccess;
    }

    /** Reduce stock (used during billing). Returns false if insufficient stock. */
    public synchronized boolean reduceStock(int productId, int quantitySold) {
        Product p = productMap.get(productId);
        if (p == null || p.getQuantity() < quantitySold) {
            return false;
        }
        int updatedQty = p.getQuantity() - quantitySold;
        return updateStock(productId, updatedQty);
    }

    public Product getProduct(int id) {
        return productMap.get(id);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }

    /** Search products in memory by name or category (case-insensitive partial match). */
    public List<Product> searchProducts(String keyword) {
        List<Product> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Product p : productMap.values()) {
            if (p.getName().toLowerCase().contains(lower) ||
                p.getCategory().toLowerCase().contains(lower)) {
                results.add(p);
            }
        }
        return results;
    }

    public int getTotalDistinctProducts() {
        return productMap.size();
    }
}
