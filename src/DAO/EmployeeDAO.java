package DAO;

import DTO.EmployeeDTO;
import DTO.RoleDTO;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private Connection con;

    private boolean openConnection(){
        try{
            con = DBConnection.getConnection();
            return con != null;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void closeConnection(){
        try{
            if(con!=null){
                con.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<EmployeeDTO> getAllEmployees(){
        ArrayList<EmployeeDTO> list = new ArrayList<>();
        if(openConnection()){
            try{
                String sql = "SELECT * FROM employees WHERE isdeleted = 0";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    EmployeeDTO emp = new EmployeeDTO();
                    emp.setId(rs.getInt("employee_id"));
                    emp.setCode(rs.getString("employee_code"));
                    emp.setFullName(rs.getString("name"));
                    emp.setUsername(rs.getString("user_name"));
                    emp.setPasswordHash(rs.getString("password_hash"));
                    emp.setPhone(rs.getString("phone"));
                    emp.setEmail(rs.getString("email"));
                    Timestamp ts = rs.getTimestamp("hire_date");
                    emp.setHireDate(ts != null ? ts.toLocalDateTime() : null);
                    emp.setSalary(rs.getBigDecimal("salary"));
                    emp.setRoleId(rs.getInt("role_id"));
                    emp.setPhotoPath(rs.getString("photo_path"));
                    list.add(emp);
                }
            }catch(SQLException e){
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return list;
    }

    public boolean updateEmployee(EmployeeDTO emp) {
        if (!openConnection()) return false;
        try {
            String sql = "UPDATE employees SET name=?, user_name=?, password_hash=?, phone=?, email=?, hire_date=?, salary=?, role_id=?, photo_path=? WHERE employee_id=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, emp.getFullName());
            pstmt.setString(2, emp.getUsername());
            pstmt.setString(3, emp.getPasswordHash());
            pstmt.setString(4, emp.getPhone());
            pstmt.setString(5, emp.getEmail());
            pstmt.setTimestamp(6, emp.getHireDate() != null ? Timestamp.valueOf(emp.getHireDate()) : null);
            pstmt.setBigDecimal(7, emp.getSalary());
            pstmt.setInt(8, emp.getRoleId());
            pstmt.setString(9, emp.getPhotoPath());
            pstmt.setInt(10, emp.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }

    public boolean softDeleteEmployee(int id) {
        if (!openConnection()) return false;
        try {
            PreparedStatement pstmt = con.prepareStatement("UPDATE employees SET isdeleted=1 WHERE employee_id=?");
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }

    public boolean addEmployee(EmployeeDTO emp){
        if(!openConnection()) return false;
        try{
            String sql = "INSERT INTO employees(employee_code, name, user_name, password_hash, phone, email, hire_date, salary, role_id, photo_path) VALUES(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, emp.getCode());
            pstmt.setString(2, emp.getFullName());
            pstmt.setString(3, emp.getUsername());
            pstmt.setString(4, emp.getPasswordHash());
            pstmt.setString(5, emp.getPhone());
            pstmt.setString(6, emp.getEmail());
            pstmt.setTimestamp(7, emp.getHireDate() != null ? Timestamp.valueOf(emp.getHireDate()) : null);
            pstmt.setBigDecimal(8, emp.getSalary());
            pstmt.setInt(9, emp.getRoleId());
            pstmt.setString(10, emp.getPhotoPath());
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally{
            closeConnection();
        }
    }

    public boolean hasEmployeeId(int id){
        if(!openConnection()) return false;
        try{
            String sql = "SELECT 1 FROM employees WHERE employee_id = ?";
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

    public boolean hasUserName(String username){
        if(!openConnection()) return false;
        try{
            String sql = "SELECT 1 FROM employees WHERE user_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally{
            closeConnection();
        }
    }

    public RoleDTO getRoleByEmployeeID(Long employeeID){
        RoleDTO role = null;
        if(openConnection()){
            try{
                String sql = "SELECT r.role_id, r.role_name, r.description " +
                     "FROM employees e " +
                     "JOIN roles r ON e.role_id = r.role_id " +
                     "WHERE e.employee_id = ?";
                
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setLong(1, employeeID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    role = new RoleDTO();
                    role.setId(rs.getInt("role_id"));
                    role.setName(rs.getString("role_name"));
                    role.setDescription(rs.getString("description"));

                    // Lấy danh sách quyền
                    role.setPermissions(getPermissionsByRoleId(role.getId()));
                }   
            }catch(SQLException e){
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return role;
    }

    public List<String> getPermissionsByRoleId(int roleID){
        List<String> permissions = new ArrayList<>();
        if(openConnection()){
            try{
                    String sql = "SELECT p.permission_name " +
                        "FROM role_permissions rp " +
                        "JOIN permissions p ON rp.permission_id = p.permission_id " +
                        "WHERE rp.role_id = ?";

                    PreparedStatement ps = con.prepareStatement(sql); 
                    ps.setLong(1, roleID);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        permissions.add(rs.getString("permission_name"));
                    }
            
            }catch(SQLException e){
                e.printStackTrace();
        
            } finally {
                closeConnection();
            }
        }
        return permissions;
    }

    //Sau khi có đăng nhập dùng bus của nhân viên rồi chọn có thể hiện panel nào

    public EmployeeDTO login(String username, String password) {
        EmployeeDTO emp = null;
        if (openConnection()) {
            try {
                String sql = "SELECT * FROM employees WHERE user_name = ? AND isdeleted = 0";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (storedHash.equals(password)) {  // Giả sử password không hash
                        emp = new EmployeeDTO();
                        emp.setId(rs.getInt("employee_id"));
                        emp.setCode(rs.getString("employee_code"));
                        emp.setFullName(rs.getString("name"));
                        emp.setUsername(rs.getString("user_name"));
                        emp.setPasswordHash(storedHash);
                        emp.setPhone(rs.getString("phone"));
                        emp.setEmail(rs.getString("email"));
                        Timestamp ts = rs.getTimestamp("hire_date");
                        emp.setHireDate(ts != null ? ts.toLocalDateTime() : null);
                        emp.setSalary(rs.getBigDecimal("salary"));
                        emp.setRoleId(rs.getInt("role_id"));
                        emp.setPhotoPath(rs.getString("photo_path"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return emp;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public EmployeeDTO getEmployeeById(int id) {
        EmployeeDTO emp = null;
        if (openConnection()) {
            try {
                String sql = "SELECT * FROM employees WHERE employee_id = ? AND isdeleted = 0";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    emp = new EmployeeDTO();
                    emp.setId(rs.getInt("employee_id"));
                    emp.setCode(rs.getString("employee_code"));
                    emp.setFullName(rs.getString("name"));
                    emp.setUsername(rs.getString("user_name"));
                    emp.setPasswordHash(rs.getString("password_hash"));
                    emp.setPhone(rs.getString("phone"));
                    emp.setEmail(rs.getString("email"));
                    Timestamp ts = rs.getTimestamp("hire_date");
                    emp.setHireDate(ts != null ? ts.toLocalDateTime() : null);
                    emp.setSalary(rs.getBigDecimal("salary"));
                    emp.setRoleId(rs.getInt("role_id"));
                    emp.setPhotoPath(rs.getString("photo_path"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return emp;
    }
}
