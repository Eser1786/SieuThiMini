package DAO;

import DTO.SalesInvoiceDiscountDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SalesInvoiceDiscountDAO {

    public boolean insert(SalesInvoiceDiscountDTO dto) {

        String sql = "INSERT INTO sales_invoice_discount (discount_id, invoice_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dto.getDiscountId());
            ps.setLong(2, dto.getInvoiceId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}