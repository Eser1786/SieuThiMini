package BUS;
import DTO.RoleDTO;
import DAO.RoleDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class RoleBUS {

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


    public RoleDAO roleDAO;
    public RoleBUS() {
        roleDAO = new RoleDAO();
    }
    public ArrayList<RoleDTO> getAllRoles() {
        return roleDAO.getAllRoles();
    }

    // public boolean addRole(RoleDTO role) {
    //     if(role.getName() == null || role.getName().isEmpty()){
    //         System.out.println("Tên vai trò không được để trống.");
    //         return false;
    //     }
        
    //     return roleDAO.addRole(role);
    // }

    public boolean addPermissionToRole(Long roleID, Long permissionID){
        if(openConnection()){
            try{
                String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setLong(1, roleID);
                ps.setLong(2, permissionID);
                return ps.executeUpdate() > 0;
            } catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
    
}
