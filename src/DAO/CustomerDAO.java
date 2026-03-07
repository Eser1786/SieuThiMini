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
                                      "&characterEncoding=utf8mb4";
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
                    customer.setCode(fixEncoding(rs.getString("customer_code")));
                    customer.setFullName(fixEncoding(rs.getString("full_name")));
                    customer.setPhone(fixEncoding(rs.getString("phone")));
                    customer.setEmail(fixEncoding(rs.getString("email")));
                    customer.setAddress(fixEncoding(rs.getString("address")));
                    customer.setLoyaltyPoints(rs.getInt("loyalty_points"));

                    Timestamp tsCreated = rs.getTimestamp("created_at");
                    customer.setCreatedAt(tsCreated != null ? tsCreated.toLocalDateTime() : null);

                    Timestamp tsLastPurchase = rs.getTimestamp("last_purchase");
                    customer.setLastPurchaseAt(tsLastPurchase != null ? tsLastPurchase.toLocalDateTime() : null);

                    customer.setTotalSpent(rs.getBigDecimal("total_spent"));
                    customer.setType(CustomerType.fromString(rs.getString("customer_type")));
                    customer.setStatus(CustomerStatus.fromString(rs.getString("status")));
                    
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

    public boolean hasCustomerCode(String code){
        boolean result = false;
        if(openConnection()){
            try{
                String sql = "SELECT * FROM customers WHERE customer_code = '" + code +"'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                result = rs.next();
            }catch(SQLException e){
                System.out.println("Không thể thực hiện tìm khách hàng theo code \n CustomerDAO - hasCustomerCode \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }

    public boolean hasCustomerPhone(String phone){
        boolean result = false;
        if(openConnection()){
            try{
                String sql = "SELECT * FROM customers WHERE phone = '" + phone +"'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                result = rs.next();
            }catch(SQLException e){
                System.out.println("Không thể thực hiện tìm khách hàng theo số điện thoại \n CustomerDAO - hasCustomerPhone \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }
    
    public CustomerDTO getCustomerByID(int id){
        CustomerDTO customer = new CustomerDTO();
        if(openConnection()){
            try{
                String sql = "SELECT * FROM customers WHERE customer_id = " + id;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if(rs.next()){
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
                    customer.setStatus(CustomerStatus.fromString(rs.getString("status")));

                }else{
                    System.out.println("Không tìm thấy id của khách hàng! \n CustomerDAO - getCustomerByID \n");
                }
            }catch(SQLException e){
                System.out.println("Không thể thực hiện việc tìm id của khách hàng \n CustomerDAO - getCustomerByID \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return customer;
    }

    public boolean updateCustomer(CustomerDTO customer) {
        boolean result = false;
        if (openConnection()) {
            try {
                String sql = "UPDATE customers SET full_name=?, phone=?, email=?, address=?, customer_type=?, status=? WHERE customer_id=?";
                PreparedStatement pstm = con.prepareStatement(sql);
                pstm.setString(1, customer.getFullName());
                pstm.setString(2, customer.getPhone());
                pstm.setString(3, customer.getEmail());
                pstm.setString(4, customer.getAddress());
                pstm.setString(5, customer.getType() != null ? customer.getType().getValue() : null);
                pstm.setString(6, customer.getStatus() != null ? customer.getStatus().getValue() : null);
                pstm.setInt(7, customer.getId());
                result = pstm.executeUpdate() >= 1;
            } catch (SQLException e) {
                System.out.println("Kh\u00f4ng th\u1ec3 c\u1eadp nh\u1eadt kh\u00e1ch h\u00e0ng! CustomerDAO - updateCustomer");
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return result;
    }

    public boolean deleteCustomer(int id) {
        boolean result = false;
        if (openConnection()) {
            try {
                PreparedStatement pstm = con.prepareStatement("DELETE FROM customers WHERE customer_id=?");
                pstm.setInt(1, id);
                result = pstm.executeUpdate() >= 1;
            } catch (SQLException e) {
                System.out.println("Kh\u00f4ng th\u1ec3 x\u00f3a kh\u00e1ch h\u00e0ng! CustomerDAO - deleteCustomer");
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }
        return result;
    }

    /** Fix UTF-8 data that was stored/returned as ISO-8859-1 (mojibake).
     *  Skip if string already contains proper Unicode chars (> U+00FF). */
    private static String fixEncoding(String s) {
        if (s == null) return null;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) > 0xFF) return s; // Already proper Unicode, no fix needed
        }
        try {
            String d = new String(s.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1),
                                  java.nio.charset.StandardCharsets.UTF_8);
            for (int i = 0; i < d.length(); i++) {
                if (d.charAt(i) > 0xFF) return d; // Successfully decoded mojibake
            }
        } catch (Exception ignored) {}
        return s;
    }
}
