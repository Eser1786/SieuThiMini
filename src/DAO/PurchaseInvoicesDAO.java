package DAO;

import DTO.PurchaseInvoicesDTO;
import DTO.PurchaseInvoiceItemsDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseInvoicesDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public List<PurchaseInvoicesDTO> getAllPurchaseInvoices() {
        List<PurchaseInvoicesDTO> list = new ArrayList<>();
        String sql = "SELECT pi.*, s.name AS supplier_name, e.name AS employee_name " +
                     "FROM purchase_invoices pi " +
                     "LEFT JOIN suppliers s ON pi.supplier_id = s.supplier_id " +
                     "LEFT JOIN employees e ON pi.employee_id = e.employee_id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PurchaseInvoicesDTO invoice = new PurchaseInvoicesDTO();
                invoice.setInvoiceId(rs.getLong("invoice_id"));
                invoice.setInvoiceCode(rs.getString("invoice_code"));
                invoice.setPurchaseId(rs.getLong("purchase_id"));
                invoice.setDateIn(rs.getTimestamp("date_in").toLocalDateTime());
                invoice.setSupplierId(rs.getLong("supplier_id"));
                invoice.setSupplierName(rs.getString("supplier_name"));
                invoice.setEmployeeId(rs.getLong("employee_id"));
                invoice.setEmployeeName(rs.getString("employee_name"));
                invoice.setSubtotal(rs.getBigDecimal("subtotal"));
                invoice.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                invoice.setTaxAmount(rs.getBigDecimal("tax_amount"));
                invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
                invoice.setPaymentMethod(rs.getString("payment_method"));
                invoice.setPaymentStatus(rs.getString("payment_status"));
                invoice.setStatus(rs.getString("status"));
                invoice.setNotes(rs.getString("notes"));
                invoice.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                invoice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                // Lấy chi tiết items
                invoice.setItems(getItemsByInvoiceId(invoice.getInvoiceId()));

                list.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private List<PurchaseInvoiceItemsDTO> getItemsByInvoiceId(Long invoiceId) {
        List<PurchaseInvoiceItemsDTO> items = new ArrayList<>();
        String sql = "SELECT pii.*, p.product_code, p.name AS product_name " +
                     "FROM purchase_invoice_items pii " +
                     "LEFT JOIN products p ON pii.product_id = p.product_id " +
                     "WHERE pii.invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PurchaseInvoiceItemsDTO item = new PurchaseInvoiceItemsDTO();
                item.setId(rs.getLong("id"));
                item.setInvoiceId(rs.getLong("invoice_id"));
                item.setProductId(rs.getLong("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getLong("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setSubtotal(rs.getBigDecimal("subtotal"));
                item.setNotes(rs.getString("notes"));

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public PurchaseInvoicesDTO getPurchaseInvoiceById(Long invoiceId) {
        PurchaseInvoicesDTO invoice = null;
        String sql = "SELECT pi.*, s.name AS supplier_name, e.name AS employee_name " +
                     "FROM purchase_invoices pi " +
                     "LEFT JOIN suppliers s ON pi.supplier_id = s.supplier_id " +
                     "LEFT JOIN employees e ON pi.employee_id = e.employee_id " +
                     "WHERE pi.invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                invoice = new PurchaseInvoicesDTO();
                invoice.setInvoiceId(rs.getLong("invoice_id"));
                invoice.setInvoiceCode(rs.getString("invoice_code"));
                invoice.setPurchaseId(rs.getLong("purchase_id"));
                invoice.setDateIn(rs.getTimestamp("date_in").toLocalDateTime());
                invoice.setSupplierId(rs.getLong("supplier_id"));
                invoice.setSupplierName(rs.getString("supplier_name"));
                invoice.setEmployeeId(rs.getLong("employee_id"));
                invoice.setEmployeeName(rs.getString("employee_name"));
                invoice.setSubtotal(rs.getBigDecimal("subtotal"));
                invoice.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                invoice.setTaxAmount(rs.getBigDecimal("tax_amount"));
                invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
                invoice.setPaymentMethod(rs.getString("payment_method"));
                invoice.setPaymentStatus(rs.getString("payment_status"));
                invoice.setStatus(rs.getString("status"));
                invoice.setNotes(rs.getString("notes"));
                invoice.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                invoice.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                // Lấy chi tiết items
                invoice.setItems(getItemsByInvoiceId(invoice.getInvoiceId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoice;
    }

    public boolean addPurchaseInvoice(PurchaseInvoicesDTO invoice) {
        boolean result = false;
        String sql = "INSERT INTO purchase_invoices (invoice_code, purchase_id, date_in, supplier_id, employee_id, subtotal, discount_amount, tax_amount, total_amount, payment_method, payment_status, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, invoice.getInvoiceCode());
            ps.setObject(2, invoice.getPurchaseId());
            ps.setTimestamp(3, Timestamp.valueOf(invoice.getDateIn()));
            ps.setObject(4, invoice.getSupplierId());
            ps.setObject(5, invoice.getEmployeeId());
            ps.setBigDecimal(6, invoice.getSubtotal());
            ps.setBigDecimal(7, invoice.getDiscountAmount());
            ps.setBigDecimal(8, invoice.getTaxAmount());
            ps.setBigDecimal(9, invoice.getTotalAmount());
            ps.setString(10, invoice.getPaymentMethod());
            ps.setString(11, invoice.getPaymentStatus());
            ps.setString(12, invoice.getStatus());
            ps.setString(13, invoice.getNotes());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    invoice.setInvoiceId(generatedKeys.getLong(1));
                }
                // Thêm chi tiết items
                addItems(invoice.getInvoiceId(), invoice.getItems());
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void addItems(Long invoiceId, List<PurchaseInvoiceItemsDTO> items) throws SQLException {
        String sql = "INSERT INTO purchase_invoice_items (invoice_id, product_id, quantity, unit_price, subtotal, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (PurchaseInvoiceItemsDTO item : items) {
                ps.setLong(1, invoiceId);
                ps.setLong(2, item.getProductId());
                ps.setLong(3, item.getQuantity());
                ps.setBigDecimal(4, item.getUnitPrice());
                ps.setBigDecimal(5, item.getSubtotal());
                ps.setString(6, item.getNotes());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    public boolean updatePurchaseInvoice(PurchaseInvoicesDTO invoice) {
        boolean result = false;
        String sql = "UPDATE purchase_invoices SET invoice_code = ?, purchase_id = ?, date_in = ?, supplier_id = ?, employee_id = ?, " +
                     "subtotal = ?, discount_amount = ?, tax_amount = ?, total_amount = ?, payment_method = ?, payment_status = ?, status = ?, notes = ?, updated_at = ? " +
                     "WHERE invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getInvoiceCode());
            ps.setObject(2, invoice.getPurchaseId());
            ps.setTimestamp(3, Timestamp.valueOf(invoice.getDateIn()));
            ps.setObject(4, invoice.getSupplierId());
            ps.setObject(5, invoice.getEmployeeId());
            ps.setBigDecimal(6, invoice.getSubtotal());
            ps.setBigDecimal(7, invoice.getDiscountAmount());
            ps.setBigDecimal(8, invoice.getTaxAmount());
            ps.setBigDecimal(9, invoice.getTotalAmount());
            ps.setString(10, invoice.getPaymentMethod());
            ps.setString(11, invoice.getPaymentStatus());
            ps.setString(12, invoice.getStatus());
            ps.setString(13, invoice.getNotes());
            ps.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(15, invoice.getInvoiceId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // Cập nhật items: xóa cũ và thêm mới
                deleteItemsByInvoiceId(invoice.getInvoiceId());
                addItems(invoice.getInvoiceId(), invoice.getItems());
                result = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean deletePurchaseInvoice(Long invoiceId) throws SQLException {
        boolean result = false;
        // Xóa items trước
        deleteItemsByInvoiceId(invoiceId);

        String sql = "DELETE FROM purchase_invoices WHERE invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void deleteItemsByInvoiceId(Long invoiceId) throws SQLException {
        String sql = "DELETE FROM purchase_invoice_items WHERE invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ps.executeUpdate();
        }
    }
}