package DAO;
import DTO.SupplierDTO;
import java.sql.*;
import java.time.LocalDateTime;

public class SupplierDAO {
    private Connection con;
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
    public boolean addSupplier(SupplierDTO supplier){
        if(openConnection()){
            try{
                String sql = "INSERT INTO suppliers (name, phone, email, address, created_at) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, supplier.getName());
                pstmt.setString(2, supplier.getPhone());
                pstmt.setString(3, supplier.getEmail());
                pstmt.setString(4, supplier.getAddress());
                pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }finally{
                closeConnection();
            }
        }
        return false;
    }
    public boolean hasProductsID(int supplierId){
        if(openConnection()){
            try{
                String sql = "SELECT COUNT(*) FROM products WHERE supplier_id = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, supplierId);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next()){
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }catch(SQLException e){
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return false;
    }

    public java.util.ArrayList<DTO.SupplierDTO> getAllSuppliers() {
        java.util.ArrayList<DTO.SupplierDTO> list = new java.util.ArrayList<>();
        if (openConnection()) {
            try {
                String sql = "SELECT supplier_id, supplier_code, name, address, contact_person, phone, email FROM suppliers WHERE isdeleted = 0 ORDER BY name";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    DTO.SupplierDTO s = new DTO.SupplierDTO();
                    s.setID(rs.getInt("supplier_id"));
                    s.setCode(rs.getString("supplier_code"));
                    s.setName(rs.getString("name"));
                    s.setAddress(rs.getString("address"));
                    s.setContactPerson(rs.getString("contact_person"));
                    s.setPhone(rs.getString("phone"));
                    s.setEmail(rs.getString("email"));
                    list.add(s);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return list;
    }

    public DTO.SupplierDTO getSupplierById(int id) {
        if (openConnection()) {
            try {
                String sql = "SELECT supplier_id, supplier_code, name, address, contact_person, phone, email FROM suppliers WHERE supplier_id = ? AND isdeleted = 0";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    DTO.SupplierDTO s = new DTO.SupplierDTO();
                    s.setID(rs.getInt("supplier_id"));
                    s.setName(rs.getString("name"));
                    s.setAddress(rs.getString("address"));
                    s.setPhone(rs.getString("phone"));
                    s.setEmail(rs.getString("email"));
                    return s;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return null;
    }

    public boolean updateSupplier(DTO.SupplierDTO supplier) {
        if (openConnection()) {
            try {
                String sql = "UPDATE suppliers SET name = ?, address = ?, phone = ?, email = ?, updated_at = ? WHERE supplier_id = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, supplier.getName());
                pstmt.setString(2, supplier.getAddress());
                pstmt.setString(3, supplier.getPhone());
                pstmt.setString(4, supplier.getEmail());
                pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setInt(6, supplier.getID());
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                closeConnection();
            }
        }
        return false;
    }

    public boolean deleteSupplier(int id) {
        if (openConnection()) {
            try {
                String sql = "UPDATE suppliers SET isdeleted = 1 WHERE supplier_id = ?";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } finally {
                closeConnection();
            }
        }
        return false;
    }

    public int getIdByCode(String code) {
        if (openConnection()) {
            try {
                String sql = "SELECT supplier_id FROM suppliers WHERE supplier_code = ? AND isdeleted = 0";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, code);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("supplier_id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return -1;
    }
}
