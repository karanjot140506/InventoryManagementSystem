package com.inventory.db;

import com.inventory.model.Bill;
import com.inventory.model.BillItem;

import java.sql.*;

/**
 * Handles persistence of bills and their line items.
 * Uses a single transaction so a bill and all its items are saved atomically.
 */
public class BillDAO {

    public int saveBill(Bill bill) {
        String billSql = "INSERT INTO bills (customer_name, bill_date, grand_total) VALUES (?, ?, ?)";
        String itemSql = "INSERT INTO bill_items (bill_id, product_id, product_name, unit_price, quantity) VALUES (?, ?, ?, ?, ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int generatedBillId;
            try (PreparedStatement ps = con.prepareStatement(billSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, bill.getCustomerName());
                ps.setTimestamp(2, Timestamp.valueOf(bill.getDateTime()));
                ps.setDouble(3, bill.getGrandTotal());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedBillId = rs.getInt(1);
                    } else {
                        con.rollback();
                        return -1;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(itemSql)) {
                for (BillItem item : bill.getItems()) {
                    ps.setInt(1, generatedBillId);
                    ps.setInt(2, item.getProductId());
                    ps.setString(3, item.getProductName());
                    ps.setDouble(4, item.getUnitPrice());
                    ps.setInt(5, item.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return generatedBillId;

        } catch (SQLException e) {
            System.err.println("Error saving bill: " + e.getMessage());
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            return -1;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
