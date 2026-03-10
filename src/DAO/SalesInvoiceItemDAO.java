package DAO;

import DTO.SalesInvoiceItemDTO;
import DAO.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesInvoiceItemDAO {

    public List<SalesInvoiceItemDTO> getByInvoiceId(Long invoiceId) {

        List<SalesInvoiceItemDTO> list = new ArrayList<>();

        String sql = """
            SELECT sii.id,
                   sii.invoice_id,
                   sii.product_id,
                   p.product_code,
                   p.name,
                   sii.quantity,
                   sii.unit_price,
                   sii.subtotal
            FROM sales_invoice_items sii
            JOIN products p ON sii.product_id = p.product_id
            WHERE sii.invoice_id = ?
        """;

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, invoiceId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                SalesInvoiceItemDTO item = new SalesInvoiceItemDTO();

                item.setId(rs.getLong("id"));
                item.setInvoiceId(rs.getLong("invoice_id"));
                item.setProductId(rs.getLong("product_id"));
                item.setProductCode(rs.getString("product_code"));
                item.setProductName(rs.getString("name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getBigDecimal("unit_price"));
                item.setSubtotal(rs.getBigDecimal("subtotal"));

                list.add(item);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
    public boolean insert(SalesInvoiceItemDTO item){

    String sql = """
        INSERT INTO sales_invoice_items
        (invoice_id, product_id, quantity, unit_price, subtotal, notes)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setLong(1, item.getInvoiceId());
        ps.setLong(2, item.getProductId());
        ps.setInt(3, item.getQuantity());
        ps.setBigDecimal(4, item.getUnitPrice());
        ps.setBigDecimal(5, item.getSubtotal());
        ps.setString(6, item.getNotes());

        return ps.executeUpdate() > 0;

    }catch(Exception e){
        e.printStackTrace();
        return false;
    }
}
public List<SalesInvoiceItemDTO> getBySaleId(Long saleId) {

    List<SalesInvoiceItemDTO> list = new ArrayList<>();

    String sql = """
        SELECT 
            sii.id,
            sii.invoice_id,
            sii.product_id,
            p.product_code,
            p.name,
            sii.quantity,
            sii.unit_price,
            sii.subtotal,
            sii.notes
        FROM sales_invoice_items sii
        JOIN products p ON sii.product_id = p.product_id
        WHERE sii.invoice_id = ?
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setLong(1, saleId);

        ResultSet rs = ps.executeQuery();

        while(rs.next()){

            SalesInvoiceItemDTO item = new SalesInvoiceItemDTO();

            item.setId(rs.getLong("id"));
            item.setInvoiceId(rs.getLong("invoice_id"));
            item.setProductId(rs.getLong("product_id"));
            item.setProductCode(rs.getString("product_code"));
            item.setProductName(rs.getString("name"));
            item.setQuantity(rs.getInt("quantity"));
            item.setUnitPrice(rs.getBigDecimal("unit_price"));
            item.setSubtotal(rs.getBigDecimal("subtotal"));
            item.setNotes(rs.getString("notes"));

            list.add(item);
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return list;
}
public List<SalesInvoiceItemDTO> getBySaleCode(String saleCode){
    
    List<SalesInvoiceItemDTO> list = new ArrayList<>();
    
    String sql = """
        SELECT 
            sii.product_id,
            sii.quantity
        FROM sales_invoice_items sii
        JOIN sales s
            ON sii.invoice_id = s.sale_id
        WHERE s.sale_code = ?
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setString(1, saleCode);

        ResultSet rs = ps.executeQuery();

        while(rs.next()){

            SalesInvoiceItemDTO item = new SalesInvoiceItemDTO();

            item.setProductId(rs.getLong("product_id"));
            item.setQuantity(rs.getInt("quantity"));

            list.add(item);
        }

    }catch(Exception e){
        e.printStackTrace();
    }

    return list;
}
}