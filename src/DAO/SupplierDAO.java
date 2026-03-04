package DAO;
import DTO.SupplierDTO;
import java.time.LocalDateTime;
import java.sql.*;

public class SupplierDAO {
    private Connection con;
    public boolean openConnection(){
        try{
            String URL = "jdbc:mysql://localhost:3307/sieuthiminiv2" +
                                      "?useSSL=false" +
                                      "&allowPublicKeyRetrieval=true" +
                                      "&serverTimezone=UTC" +
                                      "&useUnicode=true" +
                                      "&characterEncoding=UTF-8";
            String USER = "sieuthimini_user";
            String PASSWORD = "sieuthimini_pass123";
            
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public void closeConnection(){
        try{
            if(con!=null){
                con.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
}
    }
    public boolean addSupplier(SupplierDTO supplier){
        if(openConnection()){
            try{
                String sql = "INSERT INTO suppliers (name, phone, email, address, created_at) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, supplier.getName());
                pstmt.setString(2, supplier.getPhone());
                pstmt.setString(3, supplier.getEmail());
                pstmt.setString(4, supplier.getAddress());
                pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }finally{
                closeConnection();
            }
        }
        return false;
    }
    public boolean hasProductsID(int supplierId){
        if(openConnection()){
            try{
                String sql = "SELECT COUNT(*) FROM products WHERE supplier_id = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, supplierId);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()){
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }catch(SQLException e){
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return false;
    }
}
