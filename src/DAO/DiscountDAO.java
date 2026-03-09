package DAO;
import DTO.DiscountDTO;
import java.util.ArrayList;
import DAO.DBConnection;
import DTO.enums.DiscountEnum.DiscountType;
import DTO.enums.DiscountEnum.DiscountStatus;
import java.sql.*;
public class DiscountDAO {

    // ── Code generation ──────────────────────────────────────────────────────
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String generateUniqueCode() {
        java.util.Random rnd = new java.util.Random();
        String code;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++)
                sb.append(CODE_CHARS.charAt(rnd.nextInt(CODE_CHARS.length())));
            code = sb.toString();
        } while (codeExists(code));
        return code;
    }

    private boolean codeExists(String code) {
        String sql = "SELECT COUNT(*) FROM discounts WHERE discount_code = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            return false;
        }
    }
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

            String sql = "SELECT * FROM discounts WHERE is_deleted = 0";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){

                DiscountDTO d = new DiscountDTO();

                d.setId(rs.getInt("discount_id"));
                d.setDiscountCode(rs.getString("discount_code"));
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

    // Generate a unique 6-char alphanumeric code before inserting
    String code = generateUniqueCode();
    d.setDiscountCode(code);

    if(openConnection()){
        try{

            String sql = "INSERT INTO discounts(discount_code,name,description,value,discount_type,status,start_date,end_date,min_order_amount,is_auto_apply,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = con.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS
            );

            pstmt.setString(1,code);
            pstmt.setString(2,d.getName());
            pstmt.setString(3,d.getDescription());
            pstmt.setBigDecimal(4,d.getValue());
            pstmt.setString(5,d.getDiscountType().name());
            pstmt.setString(6,d.getStatus().name());
            pstmt.setDate(7,java.sql.Date.valueOf(d.getStartDate()));
            pstmt.setDate(8,java.sql.Date.valueOf(d.getEndDate()));
            pstmt.setBigDecimal(9,d.getMinOrderAmount());
            pstmt.setBoolean(10,d.getIsAutoApply());
            pstmt.setTimestamp(11,java.sql.Timestamp.valueOf(d.getCreatedAt()));
            pstmt.setTimestamp(12,java.sql.Timestamp.valueOf(d.getUpdatedAt()));

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

            String sql = "UPDATE discounts SET status = 'INACTIVE', is_deleted = 1, updated_at = NOW() WHERE discount_id = ?";
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
public DiscountDTO getDiscountByCode(String code) {
    if (openConnection()) {
        try {
            String sql = "SELECT * FROM discounts WHERE discount_code = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                DiscountDTO d = new DiscountDTO();
                d.setId(rs.getInt("discount_id"));
                d.setDiscountCode(rs.getString("discount_code"));
                d.setName(rs.getString("name"));
                d.setDiscountType(DiscountType.valueOf(rs.getString("discount_type")));
                d.setValue(rs.getBigDecimal("value"));
                d.setMinOrderAmount(rs.getBigDecimal("min_order_amount"));
                Date start = rs.getDate("start_date");
                if (start != null) d.setStartDate(start.toLocalDate());
                Date end = rs.getDate("end_date");
                if (end != null) d.setEndDate(end.toLocalDate());
                d.setDescription(rs.getString("description"));
                d.setStatus(DiscountStatus.valueOf(rs.getString("status")));
                d.setIsAutoApply(rs.getBoolean("is_auto_apply"));
                Timestamp ct = rs.getTimestamp("created_at");
                if (ct != null) d.setCreatedAt(ct.toLocalDateTime());
                Timestamp ut = rs.getTimestamp("updated_at");
                if (ut != null) d.setUpdatedAt(ut.toLocalDateTime());
                return d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    return null;
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