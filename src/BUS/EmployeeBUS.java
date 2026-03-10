package BUS;

import DAO.DBConnection;
import DAO.EmployeeDAO;
import DTO.EmployeeDTO;
import DTO.RoleDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeBUS {
    private EmployeeDAO dao;
    public EmployeeBUS(){
        dao = new EmployeeDAO();
    }
    public ArrayList<EmployeeDTO> getAllEmployees(){
        return dao.getAllEmployees();
    }
    public boolean addEmployee(EmployeeDTO emp){
        if(emp.getFullName()==null || emp.getFullName().isEmpty()){
            System.out.println("Tên nhân viên không được trống");
            return false;
        }
        if(emp.getUsername()==null || emp.getUsername().isEmpty()){
            System.out.println("Username không được trống");
            return false;
        }
        return dao.addEmployee(emp);
    }

    private String generateEmployeeCode() {
        // Logic tạo code: 'NV' + số tăng dần (tìm max code từ DB + 1)
        String sql = "SELECT MAX(employee_code) FROM employees WHERE employee_code LIKE 'NV%'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxCode = rs.getString(1);
                if (maxCode == null) {
                    return "NV001";
                }
                int num = Integer.parseInt(maxCode.substring(2)) + 1;
                return "NV" + String.format("%03d", num);  
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "default";  // Default nếu lỗi
    }


    public RoleDTO getRole(Long employeeId) {
        return dao.getRoleByEmployeeID(employeeId);
    }

    public boolean softDeleteEmployee(int id) {
        return dao.softDeleteEmployee(id);
    }

    public boolean hasPermission(Long employeeId, String permissionName) {
        RoleDTO role = getRole(employeeId);
        if (role == null) return false;
        List<String> permissions = role.getPermissions();
        return permissions.contains(permissionName);
    }

    public boolean canShowPanel(Long employeeId, String panelPermission) {
        return hasPermission(employeeId, panelPermission);
    }

    public EmployeeDTO login(String username, String password) {
        return dao.login(username, password);
    }
}
