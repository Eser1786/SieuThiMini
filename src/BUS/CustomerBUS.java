package BUS;
import DTO.CustomerDTO;

import java.sql.*;
import java.util.ArrayList;

import DAO.CustomerDAO;
import DAO.DBConnection;
public class CustomerBUS {
    private CustomerDAO customerDAO;

    public CustomerBUS() {
        customerDAO = new CustomerDAO();
    }

   public ArrayList<CustomerDTO> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    public boolean AddCustomer(CustomerDTO customer) {
        if(customer.getFullName() == null || customer.getFullName().isEmpty()){
            System.out.println("Tên khách hàng không được để trống.");
            return false;
        }
        if(customerDAO.hasCustomerCode(customer.getCode())){
            System.out.println("Mã khách hàng đã tồn tại. Vui lòng chọn mã khác.");
            return false;
        }
        if(customer.getPhone() == null || customer.getPhone().isEmpty()){
            System.out.println("Số điện thoại không được để trống.");
            return false;
        }
        if(customer.getPhone() != null && !customer.getPhone().isEmpty() && customerDAO.hasCustomerPhone(customer.getPhone())){
            System.out.println("Số điện thoại đã tồn tại. Vui lòng kiểm tra lại.");
            return false;
        }
        return customerDAO.addCustomer(customer);
    }

    private String generateCustomerCode() {
        // Logic tạo code: 'NV' + số tăng dần (tìm max code từ DB + 1)
        String sql = "SELECT MAX(customer_code) FROM customers WHERE customer_code LIKE 'KH%'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxCode = rs.getString(1);
                if (maxCode == null) {
                    return "KH001";
                }
                int num = Integer.parseInt(maxCode.substring(2)) + 1;
                return "KH" + String.format("%03d", num);  
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "default";  // Default nếu lỗi
    }
    
}
