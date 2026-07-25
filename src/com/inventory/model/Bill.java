package com.inventory.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generated bill (invoice) with multiple items.
 */
public class Bill {
    private int billId;
    private String customerName;
    private LocalDateTime dateTime;
    private List<BillItem> items;

    public Bill(int billId, String customerName) {
        this.billId = billId;
        this.customerName = customerName;
        this.dateTime = LocalDateTime.now();
        this.items = new ArrayList<>();
    }

    public void addItem(BillItem item) {
        items.add(item);
    }

    public double getGrandTotal() {
        double total = 0;
        for (BillItem item : items) {
            total += item.getLineTotal();
        }
        return total;
    }

    public int getBillId() { return billId; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getDateTime() { return dateTime; }
    public List<BillItem> getItems() { return items; }

    public String printBill() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================================\n");
        sb.append("                    SALES INVOICE\n");
        sb.append("=========================================================\n");
        sb.append("Bill No : ").append(billId).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("Date    : ").append(dateTime).append("\n");
        sb.append("---------------------------------------------------------\n");
        for (BillItem item : items) {
            sb.append(item.toString()).append("\n");
        }
        sb.append("---------------------------------------------------------\n");
        sb.append(String.format("GRAND TOTAL: Rs.%.2f\n", getGrandTotal()));
        sb.append("=========================================================\n");
        return sb.toString();
    }
}
