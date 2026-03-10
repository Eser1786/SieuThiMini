package BUS;
import DTO.SupplierDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import DAO.DBConnection;
import DAO.SupplierDAO;
public class SupplierBUS {
    private SupplierDAO supplierDAO;
    public SupplierBUS() {
        supplierDAO = new SupplierDAO();
    }

    public java.util.ArrayList<SupplierDTO> getAllSuppliers() {
        return supplierDAO.getAllSuppliers();
    }

    public SupplierDTO getSupplierById(int id) {
        return supplierDAO.getSupplierById(id);
    }

    public boolean addSupplier(SupplierDTO supplier) {
        if(supplier.getName() == null || supplier.getName().isEmpty()){
            System.out.println("Tên nhà cung cấp không được để trống.");
            return false;
        }
        if(supplier.getPhone() == null || supplier.getPhone().isEmpty()){
            System.out.println("Số điện thoại không được để trống.");
            return false;
        }
        if(supplier.getEmail() == null || supplier.getEmail().isEmpty()){
            System.out.println("Email không được để trống.");
            return false;
        }
        if(supplier.getAddress() == null || supplier.getAddress().isEmpty()){
            System.out.println("Địa chỉ không được để trống.");
            return false;
        }
        supplier.setCreatedAt(LocalDateTime.now());
        return supplierDAO.addSupplier(supplier);
    }

    public boolean updateSupplier(SupplierDTO supplier) {
        if(supplier.getName() == null || supplier.getName().isEmpty()){
            return false;
        }
        if(supplier.getPhone() == null || supplier.getPhone().isEmpty()){
            return false;
        }
        if(supplier.getEmail() == null || supplier.getEmail().isEmpty()){
            return false;
        }
        if(supplier.getAddress() == null || supplier.getAddress().isEmpty()){
            return false;
        }
        supplier.setUpdatedAt(LocalDateTime.now());
        return supplierDAO.updateSupplier(supplier);
    }

    public boolean deleteSupplier(int id) {
        return supplierDAO.deleteSupplier(id);
    }

    // helper to lookup internal id by supplier_code stored in UI table
    public int getIdByCode(String code) {
        return supplierDAO.getIdByCode(code);
    } 

    // make public so UI can generate new codes
    public String generateSupplierCode() {
        // Logic tạo code: 'NCC' + số tăng dần
        String sql = "SELECT MAX(supplier_code) FROM suppliers WHERE supplier_code LIKE 'NCC%'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxCode = rs.getString(1);
                if (maxCode == null) {
                    return "NCC001";
                }
                int num = Integer.parseInt(maxCode.substring(3)) + 1;
                return "NCC" + String.format("%03d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NCC000";  // Default nếu lỗi
    }
    
}
