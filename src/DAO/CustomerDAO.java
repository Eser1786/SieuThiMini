package DAO;

import DTO.CustomerDTO;
import DTO.enums.CustomerEnum.CustomerStatus;
import DTO.enums.CustomerEnum.CustomerType;

import java.sql.*;
import java.util.ArrayList;
// import java.time.LocalDateTime;


public class CustomerDAO {
    private Connection con;
    public boolean openConnection(){
        try{
            String URL = "jdbc:mysql://localhost:3307/sieuthiminiv2" +
                                      "?useSSL=false" +
                                      "&allowPublicKeyRetrieval=true" +
                                      "&serverTimezone=UTC" +
                                      "&useUnicode=true" +
                                      "&characterEncoding=UTF-8";
            String USER = "sieuthimini_user";
            String PASSWORD = "sieuthimini_pass123";
            
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
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

    public ArrayList<CustomerDTO> getAllCustomers(){
        ArrayList<CustomerDTO> arr = new ArrayList<CustomerDTO>();
        if(openConnection()){
            try{
                String sql = "SELECT * FROM customers";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    CustomerDTO customer = new CustomerDTO();
                    customer.setId(rs.getInt("customer_id"));
                    customer.setCode(rs.getString("customer_code"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    customer.setLoyaltyPoints(rs.getInt("loyalty_points"));

                    Timestamp tsCreated = rs.getTimestamp("created_at");
                    customer.setCreatedAt(tsCreated != null ? tsCreated.toLocalDateTime() : null);

                    Timestamp tsLastPurchase = rs.getTimestamp("last_purchase");
                    customer.setLastPurchaseAt(tsLastPurchase != null ? tsLastPurchase.toLocalDateTime() : null);

                    customer.setTotalSpent(rs.getBigDecimal("total_spent"));
                    customer.setType(CustomerType.fromString(rs.getString("customer_type")));
                    customer.setStatus(CustomerStatus.fromString(rs.getString("customer_status")));
                    
                    arr.add(customer);
                }
            }catch(Exception e){
                System.out.println("không thể trả vể danh sách customerDTO! \n CustomerDAO - getAllCustomers \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return arr;
    }

    public boolean addCustomer(CustomerDTO customer){
        boolean result = false;
        if(openConnection()){
            try{
                String sql = "INSERT INTO customers(`customer_code`,`full_name`,`phone`,`email`,`address`,`loyalty_points`,`created_at`,`last_purchase`,`total_spent`,`customer_type`,`status`) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement pstm = con.prepareStatement(sql);
                pstm.setString(1,customer.getCode());
                pstm.setString(2,customer.getFullName());
                pstm.setString(3,customer.getPhone());
                pstm.setString(4,customer.getEmail());
                pstm.setString(5,customer.getAddress());
                pstm.setInt(6, customer.getLoyaltyPoints());

                pstm.setTimestamp(7, customer.getCreatedAt() != null ? Timestamp.valueOf(customer.getCreatedAt()) : null);
                pstm.setTimestamp(8, customer.getLastPurchaseAt() != null ? Timestamp.valueOf(customer.getLastPurchaseAt()) : null);
 
                pstm.setBigDecimal(9, customer.getTotalSpent());
 
                pstm.setString(10,customer.getType() != null ? customer.getType().getValue() : null);
                pstm.setString(11, customer.getStatus() != null ? customer.getStatus().getValue() : null);

                if(pstm.executeUpdate()>=1){
                    result = true;
                }

            }catch(SQLException e){
                System.out.println("Không thể thêm khách hàng! \n CustomerDAO - addCustomer \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }

    public boolean hasCustomerID(int id){
        boolean result = false;
        if(openConnection()){
            try{
                String sql = "SELECT * FROM customers WHERE customer_id = " + id;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                result = rs.next();
            }catch(SQLException e){
                System.out.println("Không thể thực hiện tìm khách hàng! \n CustomerDAO - hasCustomerID \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }

    
}
