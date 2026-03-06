package BUS;

import DAO.DBConnection;
import DAO.ProductDAO;
import DTO.ProductDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductBUS {
    private ProductDAO dao;
    public ProductBUS(){
        dao = new ProductDAO();
    }
    public ArrayList<ProductDTO> getAllProducts(){
        return dao.getAllProducts();
    }
    public boolean addProduct(ProductDTO p){
        if(p.getName()==null || p.getName().isEmpty()){
            System.out.println("Tên sản phẩm không được trống");
            return false;
        }
        return dao.addProduct(p);
    }

    private String generateProductCode() {
        // Logic tạo code: 'NV' + số tăng dần (tìm max code từ DB + 1)
        String sql = "SELECT MAX(product_code) FROM products WHERE product_code LIKE 'SP%'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxCode = rs.getString(1);
                if (maxCode == null) {
                    return "SP001";
                }
                int num = Integer.parseInt(maxCode.substring(2)) + 1;
                return "SP" + String.format("%03d", num);  
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "default";  // Default nếu lỗi
    }
}
