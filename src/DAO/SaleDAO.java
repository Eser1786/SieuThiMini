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
                SELECT s.*, 
                       c.customer_code, 
                       c.full_name AS customer_name,
                       e.employee_code, 
                       e.name AS employee_name
                FROM sales s
                LEFT JOIN customers c ON s.customer_id = c.customer_id
                LEFT JOIN employees e ON s.employee_id = e.employee_id
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

    public SaleDTO getSaleById(int saleId) {

        String sql = """
                SELECT s.*, 
                       c.customer_code, 
                       c.full_name AS customer_name,
                       e.employee_code, 
                       e.name AS employee_name
                FROM sales s
                LEFT JOIN customers c ON s.customer_id = c.customer_id
                LEFT JOIN employees e ON s.employee_id = e.employee_id
                WHERE s.sale_id = ?
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

    public boolean addSale(SaleDTO sale) {

        String sql = """
                INSERT INTO sales
                (sale_code, sale_date, customer_id, employee_id, subtotal, discount_amount, status, payment_method, total_amount, note)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
            ps.setString(10, sale.getNote());

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

        sale.setCustomerID(rs.getInt("customer_id"));
        sale.setCustomerCode(rs.getString("customer_code"));
        sale.setCustomerName(rs.getString("customer_name"));

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
        sale.setNote(rs.getString("note"));

        return sale;
    }
    public SaleDTO getSaleByCode(String code) {

    String sql = "SELECT * FROM sales WHERE sale_code = ?";

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

}