package BUS;
import DAO.CustomerDAO;
import DAO.DBConnection;
import DTO.CustomerDTO;
import java.sql.*;
import java.util.ArrayList;
public class CustomerBUS {
    private CustomerDAO customerDAO;

    public CustomerBUS() {
        customerDAO = new CustomerDAO();
    }

   public ArrayList<CustomerDTO> getAllCustomers() {
        try {
            return customerDAO.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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

    public String generateCustomerCode() {
        
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
        return "default";  
    }

    public boolean updateCustomer(CustomerDTO customer) {
        return customerDAO.updateCustomer(customer);
    }

    public boolean updateLastPurchase(int customerId, java.time.LocalDateTime lastPurchase) {
        return customerDAO.updateLastPurchase(customerId, lastPurchase);
    }

    public boolean softDeleteCustomer(int id) {
        return customerDAO.softDeleteCustomer(id);
    }
}
