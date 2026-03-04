package DAO;
import DTO.DiscountDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import DTO.enums.DiscountEnum.DiscountType;
import DTO.enums.DiscountEnum.DiscountStatus;
import java.sql.*;
public class DiscountDAO {
    public Connection con;
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
    public ArrayList<DiscountDTO> getAllDiscounts(){
        ArrayList<DiscountDTO> arr = new ArrayList<DiscountDTO>();
        if(openConnection()){
            try{
                String sql = "SELECT * FROM discounts";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    DiscountDTO discount = new DiscountDTO();
                    discount.setId(rs.getInt("discount_id"));
                    discount.setName(rs.getString("name"));
                    discount.setDiscountType(DiscountType.valueOf(rs.getString("discount_type")));
                    discount.setValue(rs.getBigDecimal("value"));
                    discount.setMinOrderAmount(rs.getBigDecimal("min_order_amount"));
                    discount.setStartDate(rs.getDate("start_date").toLocalDate());
                    discount.setEndDate(rs.getDate("end_date").toLocalDate());
                    discount.setDescription(rs.getString("description"));
                    discount.setStatus(DiscountStatus.valueOf(rs.getString("status")));
                    discount.setIsAutoApply(rs.getBoolean("is_auto_apply"));
                    discount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    discount.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    arr.add(discount);
                }
            }catch(SQLException e){
                System.out.println("Không thể lấy danh sách discounts \n DiscountDAO - getAllDiscounts \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return arr;
    }
    public boolean addDiscount(DiscountDTO discount){
        boolean result = false;
        if(openConnection()){
            try{
                String sql = "INSERT INTO discounts (name, discount_type, value, min_order_amount, start_date, end_date, description, status, is_auto_apply, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, discount.getName());
                pstmt.setString(2, discount.getDiscountType().name());
                pstmt.setBigDecimal(3, discount.getValue());
                pstmt.setBigDecimal(4, discount.getMinOrderAmount());
                pstmt.setDate(5, Date.valueOf(discount.getStartDate()));
                pstmt.setDate(6, Date.valueOf(discount.getEndDate()));
                pstmt.setString(7, discount.getDescription());
                pstmt.setString(8, discount.getStatus().name());
                pstmt.setBoolean(9, discount.getIsAutoApply());
                pstmt.setTimestamp(10, Timestamp.valueOf(discount.getCreatedAt()));
                pstmt.setTimestamp(11, Timestamp.valueOf(discount.getUpdatedAt()));
                
                int rowsAffected = pstmt.executeUpdate();
                result = rowsAffected > 0;
            }catch(SQLException e){
                System.out.println("Không thể thêm mới discount \n DiscountDAO - addDiscount \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }
    public boolean hasDiscountID(int id){
        boolean result = false;
        if(openConnection()){
            try{
                String sql = "SELECT * FROM discounts WHERE discount_id = " + id;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                result = rs.next();
            }catch(SQLException e){
                System.out.println("không thể trả vể danh sách discounts! \n DiscountDAO - hasDiscountID \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }
}