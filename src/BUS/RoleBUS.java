package BUS;
import DTO.RoleDTO;
import DAO.RoleDAO;
import java.util.*;

public class RoleBUS {
    public RoleDAO roleDAO;
    public RoleBUS() {
        roleDAO = new RoleDAO();
    }
    public ArrayList<RoleDTO> getAllRoles() {
        return roleDAO.getAllRoles();
    }
    public boolean addRole(RoleDTO role) {
        if(role.getName() == null || role.getName().isEmpty()){
            System.out.println("Tên vai trò không được để trống.");
            return false;
        }
        
        return roleDAO.addRole(role);
    }

    
}
