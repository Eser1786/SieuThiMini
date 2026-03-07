package DAO;
import DTO.DiscountDTO;
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
    public ArrayList<DiscountDTO> getAllDiscounts() {

    ArrayList<DiscountDTO> list = new ArrayList<>();

    if(openConnection()){
        try{

            String sql = "SELECT * FROM discounts";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){

                DiscountDTO d = new DiscountDTO();

                d.setId(rs.getInt("discount_id"));
                d.setName(rs.getString("name"));
                d.setDiscountType(
                        DiscountType.valueOf(rs.getString("discount_type"))
                );

                d.setValue(rs.getBigDecimal("value"));
                d.setMinOrderAmount(rs.getBigDecimal("min_order_amount"));

                Date start = rs.getDate("start_date");
                if(start != null)
                    d.setStartDate(start.toLocalDate());

                Date end = rs.getDate("end_date");
                if(end != null)
                    d.setEndDate(end.toLocalDate());

                d.setDescription(rs.getString("description"));

                d.setStatus(
                        DiscountStatus.valueOf(rs.getString("status"))
                );

                d.setIsAutoApply(rs.getBoolean("is_auto_apply"));

                Timestamp ct = rs.getTimestamp("created_at");
                if(ct != null)
                    d.setCreatedAt(ct.toLocalDateTime());

                Timestamp ut = rs.getTimestamp("updated_at");
                if(ut != null)
                    d.setUpdatedAt(ut.toLocalDateTime());

                list.add(d);
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    return list;
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
    public boolean deleteDiscount(int id){

    boolean result = false;

    if(openConnection()){

        try{

            String sql = "UPDATE discounts SET status = 'INACTIVE', updated_at = NOW() WHERE discount_id = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();

            result = rowsAffected > 0;

        }catch(SQLException e){

            System.out.println("Không thể xóa discount (soft delete)");
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
    public boolean updateDiscount(
        int id,
        String name,
        String description,
        double value,
        String type,
        String startDate,
        String endDate,
        double minOrder,
        String status
){

    String sql = """
        UPDATE discounts
        SET name = ?,
            description = ?,
            value = ?,
            discount_type = ?,
            start_date = ?,
            end_date = ?,
            min_order_amount = ?,
            status = ?
        WHERE discount_id = ?
    """;

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setString(1, name);
        ps.setString(2, description);
        ps.setDouble(3, value);
        ps.setString(4, type);
        ps.setString(5, startDate);
        ps.setString(6, endDate);
        ps.setDouble(7, minOrder);
        ps.setString(8, status);
        ps.setInt(9, id);

        return ps.executeUpdate() > 0;

    }catch(Exception e){

        e.printStackTrace();
        return false;

    }
}
}