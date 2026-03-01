package DAO;

import DTO.CategoryDTO;

import java.sql.*;
import java.util.ArrayList;

public class CategoryDAO {
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

    public ArrayList<CategoryDTO> getAllCategory(){
        ArrayList<CategoryDTO> arr = new ArrayList<CategoryDTO>();
        if(openConnection()){
            try{
                String sql = "SELECT * FROM categories";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    CategoryDTO category = new CategoryDTO();
                    category.setID(rs.getInt("category_id"));
                    category.setName(rs.getString("name"));
                    category.setDescription(rs.getString("description"));
                    arr.add(category);
                }
            }catch(SQLException e){
                System.out.println("Không thể lấy danh sách category \n CategoryDAO - getAllCategory \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return arr;
    }

    public boolean addCategory(CategoryDTO category){
        boolean result = false;
        if(openConnection()){
            try{
                String sql = "INSERT INTO categories(`name`,`description`) VALUES (?,?)";
                PreparedStatement pstm = con.prepareStatement(sql);
                pstm.setString(1, category.getName());
                pstm.setString(2, category.getDescription());
                result = pstm.executeUpdate() > 0;
            }catch(SQLException e){
                System.out.println("Không thể thêm category \n CategoryDTO - addCategory \n");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }

    public boolean hasCategoryID(int id){
        boolean result = false;
        if(openConnection()){
            try{    
                String sql = "SELECT * FROM categories WHERE category_id = " + id;
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                result = rs.next();
            }catch(SQLException e){
                System.out.println("Không thể thực hiện tìm theo id cho category \n CategoryDAO - hasCategoryID \n  ");
                e.printStackTrace();
            }finally{
                closeConnection();
            }
        }
        return result;
    }


    

}
