package BUS;
import DTO.CustomerDTO;

import java.lang.reflect.Array;
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
        if(customer.getCode() == null || customer.getCode().isEmpty()){
            System.out.println("Mã khách hàng không được để trống.");
            return false;
        }
        if(customer.getFullName() == null || customer.getFullName().isEmpty()){
            System.out.println("Tên khách hàng không được để trống.");
            return false;
        }
        if(customerDAO.hasCustomerCode(customer.getCode())){
            System.out.println("Mã khách hàng đã tồn tại. Vui lòng chọn mã khác.");
            return false;
        }

        return customerDAO.addCustomer(customer);
    }
    
}
