package DAO;

import DTO.SaleDTO;
import DTO.enums.SaleEnum.SalePaymentMethod;
import DTO.enums.SaleEnum.SaleStatus;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class SaleDAO {

    public ArrayList<SaleDTO> getAllSales() {

        ArrayList<SaleDTO> list = new ArrayList<>();

        String sql = """
            SELECT s.sale_id, s.sale_code, s.sale_date, s.customer_id, s.employee_id,
                   s.subtotal, s.discount_amount, s.status, s.payment_method,
                   s.total_amount, s.note, s.isdeleted,
                   COALESCE((SELECT SUM(sii.quantity)
                     FROM sales_invoices si
                     JOIN sales_invoice_items sii ON si.invoice_id = sii.invoice_id
                     WHERE si.sale_id = s.sale_id), s.total_quantity) AS total_quantity,
                   c.customer_code, c.full_name AS customer_name,
                   c.phone AS customer_phone, c.address AS customer_address,
                   e.employee_code, e.name AS employee_name
            FROM sales s
            LEFT JOIN customers c ON s.customer_id = c.customer_id
            LEFT JOIN employees e ON s.employee_id = e.employee_id
            WHERE s.isdeleted = 0
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToSale(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<SaleDTO> getSalesByStatus(SaleStatus status) {

        ArrayList<SaleDTO> list = new ArrayList<>();

        String sql = """
            SELECT s.sale_id, s.sale_code, s.sale_date, s.customer_id, s.employee_id,
                   s.subtotal, s.discount_amount, s.status, s.payment_method,
                   s.total_amount, s.note, s.isdeleted,
                   COALESCE((SELECT SUM(sii.quantity)
                     FROM sales_invoices si
                     JOIN sales_invoice_items sii ON si.invoice_id = sii.invoice_id
                     WHERE si.sale_id = s.sale_id), s.total_quantity) AS total_quantity,
                   c.customer_code, c.full_name AS customer_name,
                   c.phone AS customer_phone, c.address AS customer_address,
                   e.employee_code, e.name AS employee_name
            FROM sales s
            LEFT JOIN customers c ON s.customer_id = c.customer_id
            LEFT JOIN employees e ON s.employee_id = e.employee_id
            WHERE s.status = ? AND s.isdeleted = 0
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.getValue());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSale(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public SaleDTO getSaleById(int saleId) {

        String sql = """
            SELECT s.sale_id, s.sale_code, s.sale_date, s.customer_id, s.employee_id,
                   s.subtotal, s.discount_amount, s.status, s.payment_method,
                   s.total_amount, s.note, s.isdeleted,
                   COALESCE((SELECT SUM(sii.quantity)
                     FROM sales_invoices si
                     JOIN sales_invoice_items sii ON si.invoice_id = sii.invoice_id
                     WHERE si.sale_id = s.sale_id), s.total_quantity) AS total_quantity,
                   c.customer_code, c.full_name AS customer_name,
                   c.phone AS customer_phone, c.address AS customer_address,
                   e.employee_code, e.name AS employee_name
            FROM sales s
            LEFT JOIN customers c ON s.customer_id = c.customer_id
            LEFT JOIN employees e ON s.employee_id = e.employee_id
            WHERE s.sale_id = ? AND s.isdeleted = 0
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, saleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSale(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<SaleDTO> getAllSalesByDateRange(LocalDate from, LocalDate to) {

        ArrayList<SaleDTO> list = new ArrayList<>();

        String sql = """
            SELECT s.sale_id, s.sale_code, s.sale_date, s.customer_id, s.employee_id,
                   s.subtotal, s.discount_amount, s.status, s.payment_method,
                   s.total_amount, s.note, s.isdeleted,
                   COALESCE((SELECT SUM(sii.quantity)
                     FROM sales_invoices si
                     JOIN sales_invoice_items sii ON si.invoice_id = sii.invoice_id
                     WHERE si.sale_id = s.sale_id), s.total_quantity) AS total_quantity,
                   c.customer_code, c.full_name AS customer_name,
                   c.phone AS customer_phone, c.address AS customer_address,
                   e.employee_code, e.name AS employee_name
            FROM sales s
            LEFT JOIN customers c ON s.customer_id = c.customer_id
            LEFT JOIN employees e ON s.employee_id = e.employee_id
            WHERE s.sale_date BETWEEN ? AND ? AND s.isdeleted = 0
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSale(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean addSale(SaleDTO sale) {

        String sql = """
                INSERT INTO sales
                (sale_code, sale_date, customer_id, employee_id, subtotal, discount_amount, status, payment_method, total_amount, total_quantity, note, isdeleted)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sale.getSaleCode());

            LocalDate saleDate = sale.getSaleDate();
            if (saleDate != null) {
                ps.setDate(2, Date.valueOf(saleDate));
            } else {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            }

            ps.setInt(3, sale.getCustomerID());
            ps.setInt(4, sale.getEmployeeID());
            ps.setBigDecimal(5, sale.getSubTotal());
            ps.setBigDecimal(6, sale.getDiscountAmount());
            ps.setString(7, sale.getSaleStatus() != null ? sale.getSaleStatus().getValue() : null);
            ps.setString(8, sale.getSalePaymentMethod() != null ? sale.getSalePaymentMethod().getValue() : null);
            ps.setBigDecimal(9, sale.getTotalAmount());
            ps.setInt(10, sale.getTotalQuantity());
            ps.setString(11, sale.getNote());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasSaleId(int id) {

        String sql = "SELECT 1 FROM sales WHERE sale_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private SaleDTO mapRowToSale(ResultSet rs) throws SQLException {

        SaleDTO sale = new SaleDTO();

        sale.setSaleID(rs.getInt("sale_id"));
        sale.setSaleCode(rs.getString("sale_code"));
        
        // Set sale date
        Date saleDate = rs.getDate("sale_date");
        if (saleDate != null) {
            sale.setSaleDate(saleDate.toLocalDate());
        }

        sale.setCustomerID(rs.getInt("customer_id"));
        sale.setCustomerCode(fixEncoding(rs.getString("customer_code")));
        sale.setCustomerName(fixEncoding(rs.getString("customer_name")));
        sale.setCustomerPhone(fixEncoding(rs.getString("customer_phone")));
        sale.setCustomerAddress(fixEncoding(rs.getString("customer_address")));

        sale.setEmployeeID(rs.getInt("employee_id"));
        sale.setEmployeeCode(rs.getString("employee_code"));
        sale.setEmployeeName(rs.getString("employee_name"));

        sale.setSubTotal(rs.getBigDecimal("subtotal"));
        sale.setDiscountAmount(rs.getBigDecimal("discount_amount"));

        String status = rs.getString("status");
        if (status != null) {
            sale.setSaleStatus(SaleStatus.fromString(status));
        }

        String pm = rs.getString("payment_method");
        if (pm != null) {
            sale.setPaymentMethod(SalePaymentMethod.fromString(pm));
        }

        sale.setTotalAmount(rs.getBigDecimal("total_amount"));
        sale.setTotalQuantity(rs.getInt("total_quantity"));
        sale.setNote(rs.getString("note"));
        sale.setIsdeleted(rs.getBoolean("isdeleted"));

        return sale;
    }
    public SaleDTO getSaleByCode(String code) {

        String sql = """
                SELECT s.sale_id, s.sale_code, s.sale_date, s.customer_id, s.employee_id,
                       s.subtotal, s.discount_amount, s.status, s.payment_method,
                       s.total_amount, s.note, s.isdeleted,
                       COALESCE((SELECT SUM(sii.quantity)
                                 FROM sales_invoices si
                                 JOIN sales_invoice_items sii ON si.invoice_id = sii.invoice_id
                                 WHERE si.sale_id = s.sale_id), s.total_quantity) AS total_quantity,
                       c.customer_code, c.full_name AS customer_name,
                       c.phone AS customer_phone, c.address AS customer_address,
                       e.employee_code, e.name AS employee_name
                FROM sales s
                LEFT JOIN customers c ON s.customer_id = c.customer_id
                LEFT JOIN employees e ON s.employee_id = e.employee_id
                WHERE s.sale_code = ? AND s.isdeleted = 0
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToSale(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public boolean updateStatus(String saleCode, SaleStatus newStatus) {
        String sql = "UPDATE sales SET status = ? WHERE sale_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newStatus.getValue());
            ps.setString(2, saleCode);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteSale(String saleCode) {
        String sql = "UPDATE sales SET isdeleted = 1 WHERE sale_code = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, saleCode);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String fixEncoding(String s) {
        if (s == null) return null;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > 0xFF) return s; // Already proper Unicode, no fix needed
        }
        try {
            String d = new String(s.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1),
                                  java.nio.charset.StandardCharsets.UTF_8);
            for (int i = 0; i < d.length(); i++) {
                if (d.charAt(i) > 0xFF) return d; // Successfully decoded mojibake
            }
        } catch (Exception ignored) {}
        return s;
    }
}