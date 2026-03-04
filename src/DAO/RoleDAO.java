package DAO;
import DTO.RoleDTO; 
import java.sql.*;
import java.util.*;
public class RoleDAO {
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
    public ArrayList<RoleDTO> getAllRoles(){
        ArrayList<RoleDTO> arr = new ArrayList<RoleDTO>();
        if(openConnection()){
            try{
                String sql = "SELECT * FROM roles";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    RoleDTO role = new RoleDTO();
                    role.setId(rs.getInt("role_id"));
                    role.setName(rs.getString("name"));
                    role.setDescription(rs.getString("description"));
                    arr.add(role);
                }
            }catch(SQLException e){
                System.out.println("Không thể lấy dữ liệu từ bảng roles");
            }finally{
                closeConnection();
            }
        }
        return arr;
    }
    public boolean addRole(RoleDTO role){
        if(openConnection()){
            try{
                String sql = "INSERT INTO roles (name, description) VALUES (?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, role.getName());
                pstmt.setString(2, role.getDescription());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }catch(SQLException e){
                System.out.println("Không thể thêm vai trò mới");
                e.printStackTrace();
                return false;
            }finally{
                closeConnection();
            }
        }
        return false;
    }
}
