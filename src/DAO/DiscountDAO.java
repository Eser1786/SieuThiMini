package DAO;
import DTO.DiscountDTO;
import java.util.ArrayList;
import DAO.DBConnection;
import DTO.enums.DiscountEnum.DiscountType;
import DTO.enums.DiscountEnum.DiscountStatus;
import java.sql.*;
public class DiscountDAO {
    public Connection con;
    public boolean openConnection(){
        try{
            con = DBConnection.getConnection();
            return con != null;
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
   public int addDiscount(DiscountDTO d){

    int discountId = -1;

    if(openConnection()){
        try{

            String sql = "INSERT INTO discounts(name,description,value,discount_type,status,start_date,end_date,min_order_amount,is_auto_apply,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = con.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS
            );

            pstmt.setString(1,d.getName());
            pstmt.setString(2,d.getDescription());
            pstmt.setBigDecimal(3,d.getValue());
            pstmt.setString(4,d.getDiscountType().name());
            pstmt.setString(5,d.getStatus().name());
            pstmt.setDate(6,java.sql.Date.valueOf(d.getStartDate()));
            pstmt.setDate(7,java.sql.Date.valueOf(d.getEndDate()));
            pstmt.setBigDecimal(8,d.getMinOrderAmount());
            pstmt.setBoolean(9,d.getIsAutoApply());
            pstmt.setTimestamp(10,java.sql.Timestamp.valueOf(d.getCreatedAt()));
            pstmt.setTimestamp(11,java.sql.Timestamp.valueOf(d.getUpdatedAt()));

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();

            if(rs.next()){
                discountId = rs.getInt(1);
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    return discountId;
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
public void insertDiscountProduct(int discountId, int productId){

    String sql = "INSERT INTO discount_products(discount_id, product_id) VALUES (?,?)";

    try(Connection conn = DBConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){

        ps.setInt(1, discountId);
        ps.setInt(2, productId);

        ps.executeUpdate();

    }catch(Exception e){
        e.printStackTrace();
    }
}
public int getDiscountIdByNameAndCreatedAt(String name, Timestamp createdAt){

    int id = -1;

    if(openConnection()){
        try{

            String sql = "SELECT discount_id FROM discounts WHERE name = ? AND created_at = ? LIMIT 1";

            PreparedStatement pstmt = con.prepareStatement(sql);

            pstmt.setString(1, name);
            pstmt.setTimestamp(2, createdAt);

            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                id = rs.getInt("discount_id");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }

    return id;
}
}