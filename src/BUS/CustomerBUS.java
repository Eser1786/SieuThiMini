package BUS;
import DTO.CustomerDTO;

import java.util.ArrayList;

import DAO.CustomerDAO;
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
    
}
