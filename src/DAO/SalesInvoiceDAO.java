package DAO;

import DTO.SalesInvoiceDTO;
import DTO.SalesInvoiceItemDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesInvoiceDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public List<SalesInvoiceDTO> getAllSalesInvoices() {
        List<SalesInvoiceDTO> list = new ArrayList<>();
        String sql = "SELECT si.*, c.full_name AS customer_name, c.phone AS customer_phone, e.name AS employee_name " +
                     "FROM sales_invoices si " +
                     "LEFT JOIN customers c ON si.customer_id = c.customer_id " +
                     "LEFT JOIN employees e ON si.employee_id = e.employee_id";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SalesInvoiceDTO invoice = new SalesInvoiceDTO();
                invoice.setInvoiceId(rs.getLong("invoice_id"));
                invoice.setInvoiceCode(rs.getString("invoice_code"));
                invoice.setSaleId(rs.getLong("sale_id"));
                invoice.setCustomerId(rs.getLong("customer_id"));
                invoice.setCustomerName(rs.getString("customer_name"));
                invoice.setCustomerPhone(rs.getString("customer_phone"));
                invoice.setEmployeeId(rs.getLong("employee_id"));
                invoice.setEmployeeName(rs.getString("employee_name"));
                invoice.setSubtotal(rs.getBigDecimal("subtotal"));
                invoice.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                invoice.setTaxAmount(rs.getBigDecimal("tax_amount"));
                invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
                invoice.setPaymentMethod(rs.getString("payment_method"));
                invoice.setStatus(rs.getString("status"));

                // Lấy chi tiết items (sales_invoice_items)
                invoice.setItems(getItemsByInvoiceId(invoice.getInvoiceId()));

                list.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private List<SalesInvoiceItemDTO> getItemsByInvoiceId(Long invoiceId) {
        List<SalesInvoiceItemDTO> items = new ArrayList<>();
        String sql = "SELECT sii.*, p.product_code, p.name AS product_name " +
                     "FROM sales_invoice_items sii " +
                     "LEFT JOIN products p ON sii.product_id = p.product_id " +
                     "WHERE sii.invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SalesInvoiceItemDTO item = new SalesInvoiceItemDTO();
                item.setId(rs.getLong("id"));
                item.setInvoiceId(rs.getLong("invoice_id"));
                item.setProductId(rs.getLong("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
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

    public boolean addSalesInvoice(SalesInvoiceDTO invoice) {
        boolean result = false;
        String sql = "INSERT INTO sales_invoices (invoice_code, sale_id, customer_id, employee_id, subtotal, discount_amount, tax_amount, total_amount, payment_method, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, invoice.getInvoiceCode());
            ps.setObject(2, invoice.getSaleId());
            ps.setObject(3, invoice.getCustomerId());
            ps.setObject(4, invoice.getEmployeeId());
            ps.setBigDecimal(5, invoice.getSubtotal());
            ps.setBigDecimal(6, invoice.getDiscountAmount());
            ps.setBigDecimal(7, invoice.getTaxAmount());
            ps.setBigDecimal(8, invoice.getTotalAmount());
            ps.setString(9, invoice.getPaymentMethod());
            ps.setString(10, invoice.getStatus());

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

    private void addItems(Long invoiceId, List<SalesInvoiceItemDTO> items) throws SQLException {
        String sql = "INSERT INTO sales_invoice_items (invoice_id, product_id, quantity, unit_price, subtotal, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (SalesInvoiceItemDTO item : items) {
                ps.setLong(1, invoiceId);
                ps.setLong(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getUnitPrice());
                ps.setBigDecimal(5, item.getSubtotal());
                ps.setString(6, item.getNotes());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    public SalesInvoiceDTO getSalesInvoiceById(Long invoiceId) {
        SalesInvoiceDTO invoice = null;
        String sql = "SELECT si.*, c.full_name AS customer_name, c.phone AS customer_phone, e.name AS employee_name " +
                     "FROM sales_invoices si " +
                     "LEFT JOIN customers c ON si.customer_id = c.customer_id " +
                     "LEFT JOIN employees e ON si.employee_id = e.employee_id " +
                     "WHERE si.invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                invoice = new SalesInvoiceDTO();
                invoice.setInvoiceId(rs.getLong("invoice_id"));
                invoice.setInvoiceCode(rs.getString("invoice_code"));
                invoice.setSaleId(rs.getLong("sale_id"));
                invoice.setCustomerId(rs.getLong("customer_id"));
                invoice.setCustomerName(rs.getString("customer_name"));
                invoice.setCustomerPhone(rs.getString("customer_phone"));
                invoice.setEmployeeId(rs.getLong("employee_id"));
                invoice.setEmployeeName(rs.getString("employee_name"));
                invoice.setSubtotal(rs.getBigDecimal("subtotal"));
                invoice.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                invoice.setTaxAmount(rs.getBigDecimal("tax_amount"));
                invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
                invoice.setPaymentMethod(rs.getString("payment_method"));
                invoice.setStatus(rs.getString("status"));

                // Lấy chi tiết items
                invoice.setItems(getItemsByInvoiceId(invoice.getInvoiceId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoice;
    }

    public boolean updateSalesInvoice(SalesInvoiceDTO invoice) {
        boolean result = false;
        String sql = "UPDATE sales_invoices SET invoice_code = ?, sale_id = ?, customer_id = ?, employee_id = ?, " +
                     "subtotal = ?, discount_amount = ?, tax_amount = ?, total_amount = ?, payment_method = ?, status = ? " +
                     "WHERE invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, invoice.getInvoiceCode());
            ps.setObject(2, invoice.getSaleId());
            ps.setObject(3, invoice.getCustomerId());
            ps.setObject(4, invoice.getEmployeeId());
            ps.setBigDecimal(5, invoice.getSubtotal());
            ps.setBigDecimal(6, invoice.getDiscountAmount());
            ps.setBigDecimal(7, invoice.getTaxAmount());
            ps.setBigDecimal(8, invoice.getTotalAmount());
            ps.setString(9, invoice.getPaymentMethod());
            ps.setString(10, invoice.getStatus());
            ps.setLong(11, invoice.getInvoiceId());

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

    public boolean deleteSalesInvoice(Long invoiceId) throws SQLException {
        boolean result = false;
        // Xóa items trước
        deleteItemsByInvoiceId(invoiceId);

        String sql = "DELETE FROM sales_invoices WHERE invoice_id = ?";

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
        String sql = "DELETE FROM sales_invoice_items WHERE invoice_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, invoiceId);
            ps.executeUpdate();
        }
    }

    // Các phương thức khác: update, delete, getById... (tương tự add, dùng JOIN để lấy customer/employee name)
}