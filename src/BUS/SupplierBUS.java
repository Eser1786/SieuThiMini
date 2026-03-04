package BUS;
import DTO.SupplierDTO;
import java.time.LocalDateTime;
import java.sql.*;
import DAO.SupplierDAO;
public class SupplierBUS {
    private SupplierDAO supplierDAO;
    public SupplierBUS() {
        supplierDAO = new SupplierDAO();
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
    
}
