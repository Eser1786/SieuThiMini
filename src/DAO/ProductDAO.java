package DAO;

import java.sql.*;

public class ProductDAO {

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

   public java.util.ArrayList<DTO.ProductDTO> getAllProducts(){
    java.util.ArrayList<DTO.ProductDTO> list = new java.util.ArrayList<>();
    if(openConnection()){
        try{
            String sql = """
                SELECT p.*, s.name AS supplier_name
                FROM products p
                LEFT JOIN suppliers s 
                ON p.supplier_id = s.supplier_id
            """;

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                DTO.ProductDTO p = new DTO.ProductDTO();

                p.setId(rs.getInt("product_id"));
                p.setCode(rs.getString("product_code"));
                p.setImagePath(rs.getString("image_path")); // thêm trường đường dẫn ảnh
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));

                // 🔥 Supplier
                DTO.SupplierDTO sup = new DTO.SupplierDTO();
                sup.setID(rs.getInt("supplier_id"));
                sup.setName(rs.getString("supplier_name")); // 👈 LẤY TÊN Ở ĐÂY
                p.setSupplier(sup);

                // Category giữ nguyên nếu chưa cần name
                DTO.CategoryDTO cat = new DTO.CategoryDTO();
                cat.setID(rs.getInt("category_id"));
                p.setCategory(cat);

                p.setCostPrice(rs.getBigDecimal("cost_price"));
                p.setSellingPrice(rs.getBigDecimal("selling_price"));
                p.setTotalQuantity(rs.getLong("total_quantity"));
                p.setMinStockLevel(rs.getLong("min_stock_level"));
                p.setMadeIn(rs.getString("made_in"));

                java.sql.Date pd = rs.getDate("production_date");
                if(pd!=null) p.setProductionDate(pd.toLocalDate());

                java.sql.Date ed = rs.getDate("expire_date");
                if(ed!=null) p.setExpireDate(ed.toLocalDate());

                p.setPosition(rs.getString("position"));
                p.setUnit(rs.getString("unit"));
                p.setStatus(rs.getString("status"));
                p.setIsVisible(rs.getBoolean("is_visible"));

                Timestamp ct = rs.getTimestamp("created_at");
                if(ct!=null) p.setCreatedAt(ct.toLocalDateTime());

                Timestamp ut = rs.getTimestamp("updated_at");
                if(ut!=null) p.setUpdatedAt(ut.toLocalDateTime());

                list.add(p);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            closeConnection();
        }
    }
    return list;
}

    public boolean addProduct(DTO.ProductDTO p){
        if(!openConnection()) return false;
        try{
            String sql = "INSERT INTO products(product_code,image_path,name,description,category_id,supplier_id,cost_price,selling_price,total_quantity,min_stock_level,made_in,production_date,expire_date,position,unit,status,is_visible,created_at,updated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, p.getCode());
            pstmt.setString(2, p.getImagePath()); // đường dẫn hình
            pstmt.setString(3, p.getName());
            pstmt.setString(4, p.getDescription());
            pstmt.setInt(5, p.getCategory()!=null ? p.getCategory().getID() : 0);
            pstmt.setInt(6, p.getSupplier()!=null ? p.getSupplier().getID() : 0);
            pstmt.setBigDecimal(7, p.getCostPrice());
            pstmt.setBigDecimal(8, p.getSellingPrice());
            pstmt.setLong(9, p.getTotalQuantity());
            pstmt.setLong(10, p.getMinStockLevel());
            pstmt.setString(11, p.getMadeIn());
            pstmt.setDate(12, p.getProductionDate()!=null ? java.sql.Date.valueOf(p.getProductionDate()) : null);
            pstmt.setDate(13, p.getExpireDate()!=null ? java.sql.Date.valueOf(p.getExpireDate()) : null);
            pstmt.setString(14, p.getPosition());
            pstmt.setString(15, p.getUnit());
            pstmt.setString(16, p.getStatus());
            pstmt.setBoolean(17, p.getIsVisible());
            pstmt.setTimestamp(18, p.getCreatedAt()!=null ? Timestamp.valueOf(p.getCreatedAt()) : null);
            pstmt.setTimestamp(19, p.getUpdatedAt()!=null ? Timestamp.valueOf(p.getUpdatedAt()) : null);
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally{
            closeConnection();
        }
    }
    
    public boolean hasProductId(int id){
        if(!openConnection()) return false;
        try{
            String sql = "SELECT 1 FROM products WHERE product_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally{
            closeConnection();
        }
    }
    
}
