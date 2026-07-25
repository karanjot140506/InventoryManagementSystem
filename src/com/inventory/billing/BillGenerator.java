package com.inventory.billing;

import com.inventory.db.BillDAO;
import com.inventory.manager.InventoryManager;
import com.inventory.model.Bill;
import com.inventory.model.BillItem;
import com.inventory.model.Product;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates bills: validates stock, deducts quantity, and persists the bill.
 */
public class BillGenerator {

    private final InventoryManager inventoryManager;
    private final BillDAO billDAO = new BillDAO();
    private final AtomicInteger localBillCounter = new AtomicInteger(1000);

    public BillGenerator(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    /**
     * Creates a bill for one product line item. Call multiple times on the
     * same Bill object to add more items before finalizing.
     */
    public boolean addItemToBill(Bill bill, int productId, int quantity) {
        Product p = inventoryManager.getProduct(productId);
        if (p == null) {
            System.out.println("Product ID " + productId + " does not exist.");
            return false;
        }
        if (p.getQuantity() < quantity) {
            System.out.println("Insufficient stock for '" + p.getName() + "'. Available: " + p.getQuantity());
            return false;
        }

        boolean reduced = inventoryManager.reduceStock(productId, quantity);
        if (!reduced) {
            System.out.println("Failed to reduce stock for '" + p.getName() + "'.");
            return false;
        }

        bill.addItem(new BillItem(p.getId(), p.getName(), p.getPrice(), quantity));
        return true;
    }

    /** Finalize and persist the bill. Returns the saved bill ID (or -1 on failure). */
    public int finalizeBill(Bill bill) {
        if (bill.getItems().isEmpty()) {
            System.out.println("Cannot finalize an empty bill.");
            return -1;
        }
        int id = billDAO.saveBill(bill);
        if (id == -1) {
            System.out.println("Warning: bill could not be saved to database (in-memory total still valid).");
        }
        return id;
    }

    public int nextLocalBillId() {
        return localBillCounter.incrementAndGet();
    }
}
