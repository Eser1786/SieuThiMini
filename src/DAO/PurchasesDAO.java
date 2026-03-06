package DAO;

import DTO.PurchasesDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchasesDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public List<PurchasesDTO> getAllPurchases() {
        List<PurchasesDTO> list = new ArrayList<>();
        String sql = "SELECT p.*, s.name AS supplier_name, e.name AS employee_name " +
                     "FROM purchases p " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN employees e ON p.employee_id = e.employee_id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PurchasesDTO purchase = new PurchasesDTO();
                purchase.setPurchaseId(rs.getLong("purchase_id"));
                purchase.setPurchaseCode(rs.getString("purchase_code"));
                purchase.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
                purchase.setSupplierId(rs.getLong("supplier_id"));
                purchase.setSupplierName(rs.getString("supplier_name"));
                purchase.setEmployeeId(rs.getLong("employee_id"));
                purchase.setEmployeeName(rs.getString("employee_name"));
                purchase.setSubtotal(rs.getBigDecimal("subtotal"));
                purchase.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                purchase.setTaxAmount(rs.getBigDecimal("tax_amount"));
                purchase.setTotalAmount(rs.getBigDecimal("total_amount"));
                purchase.setStatus(rs.getString("status"));
                purchase.setNotes(rs.getString("notes"));
                purchase.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                purchase.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                list.add(purchase);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public PurchasesDTO getPurchaseById(Long purchaseId) {
        PurchasesDTO purchase = null;
        String sql = "SELECT p.*, s.name AS supplier_name, e.name AS employee_name " +
                     "FROM purchases p " +
                     "LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "LEFT JOIN employees e ON p.employee_id = e.employee_id " +
                     "WHERE p.purchase_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, purchaseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                purchase = new PurchasesDTO();
                purchase.setPurchaseId(rs.getLong("purchase_id"));
                purchase.setPurchaseCode(rs.getString("purchase_code"));
                purchase.setPurchaseDate(rs.getTimestamp("purchase_date").toLocalDateTime());
                purchase.setSupplierId(rs.getLong("supplier_id"));
                purchase.setSupplierName(rs.getString("supplier_name"));
                purchase.setEmployeeId(rs.getLong("employee_id"));
                purchase.setEmployeeName(rs.getString("employee_name"));
                purchase.setSubtotal(rs.getBigDecimal("subtotal"));
                purchase.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                purchase.setTaxAmount(rs.getBigDecimal("tax_amount"));
                purchase.setTotalAmount(rs.getBigDecimal("total_amount"));
                purchase.setStatus(rs.getString("status"));
                purchase.setNotes(rs.getString("notes"));
                purchase.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                purchase.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return purchase;
    }

    public boolean addPurchase(PurchasesDTO purchase) {
        boolean result = false;
        String sql = "INSERT INTO purchases (purchase_code, purchase_date, supplier_id, employee_id, subtotal, discount_amount, tax_amount, total_amount, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, purchase.getPurchaseCode());
            ps.setTimestamp(2, Timestamp.valueOf(purchase.getPurchaseDate()));
            ps.setLong(3, purchase.getSupplierId());
            ps.setLong(4, purchase.getEmployeeId());
            ps.setBigDecimal(5, purchase.getSubtotal());
            ps.setBigDecimal(6, purchase.getDiscountAmount());
            ps.setBigDecimal(7, purchase.getTaxAmount());
            ps.setBigDecimal(8, purchase.getTotalAmount());
            ps.setString(9, purchase.getStatus());
            ps.setString(10, purchase.getNotes());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    purchase.setPurchaseId(generatedKeys.getLong(1));
                }
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean updatePurchase(PurchasesDTO purchase) {
        boolean result = false;
        String sql = "UPDATE purchases SET purchase_code = ?, purchase_date = ?, supplier_id = ?, employee_id = ?, " +
                     "subtotal = ?, discount_amount = ?, tax_amount = ?, total_amount = ?, status = ?, notes = ?, updated_at = ? " +
                     "WHERE purchase_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, purchase.getPurchaseCode());
            ps.setTimestamp(2, Timestamp.valueOf(purchase.getPurchaseDate()));
            ps.setLong(3, purchase.getSupplierId());
            ps.setLong(4, purchase.getEmployeeId());
            ps.setBigDecimal(5, purchase.getSubtotal());
            ps.setBigDecimal(6, purchase.getDiscountAmount());
            ps.setBigDecimal(7, purchase.getTaxAmount());
            ps.setBigDecimal(8, purchase.getTotalAmount());
            ps.setString(9, purchase.getStatus());
            ps.setString(10, purchase.getNotes());
            ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(12, purchase.getPurchaseId());

            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean deletePurchase(Long purchaseId) {
        boolean result = false;
        String sql = "DELETE FROM purchases WHERE purchase_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, purchaseId);
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}