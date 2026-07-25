package com.inventory.model;

/**
 * Represents a single line item within a Bill.
 */
public class BillItem {
    private int productId;
    private String productName;
    private double unitPrice;
    private int quantity;

    public BillItem(int productId, String productName, double unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public double getLineTotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return String.format("%-20s x%-5d @ Rs.%-10.2f = Rs.%.2f",
                productName, quantity, unitPrice, getLineTotal());
    }
}
