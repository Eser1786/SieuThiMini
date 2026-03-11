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

    /** 1 \u0111i\u1ec3m m\u1ed7i 10.000 VN\u0110. Sau khi c\u1ed9ng \u0111i\u1ec3m t\u1ef1 \u0111\u1ed9ng c\u1eadp nh\u1eadt h\u1ea1ng. */
    public boolean addLoyaltyPoints(int customerId, long totalAmount) {
        int earned = (int)(totalAmount / 10000);
        if (earned <= 0) return true;
        CustomerDTO c = customerDAO.getCustomerByID(customerId);
        if (c == null || c.getId() == 0) return false;
        int newPoints = c.getLoyaltyPoints() + earned;
        return customerDAO.updateLoyaltyAndType(customerId, newPoints, tierFromPoints(newPoints));
    }

    public static DTO.enums.CustomerEnum.CustomerType tierFromPoints(int points) {
        if (points >= 2000) return DTO.enums.CustomerEnum.CustomerType.DIAMOND;
        if (points >= 500)  return DTO.enums.CustomerEnum.CustomerType.GOLD;
        if (points >= 100)  return DTO.enums.CustomerEnum.CustomerType.SILVER;
        return DTO.enums.CustomerEnum.CustomerType.REGULAR;
    }
}
