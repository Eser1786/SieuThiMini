package DAO;



import java.sql.*;



public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/sieuthiminiv2" +
                                      "?useSSL=false" +
                                      "&allowPublicKeyRetrieval=true" +
                                      "&serverTimezone=UTC" +
                                      "&useUnicode=true" +
                                      "&characterEncoding=UTF-8";

    private static final String USER = "sieuthimini_user";
    private static final String PASSWORD = "sieuthimini_pass123";

    private static Connection con;

    public static Boolean openConnection(){
        try{
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnection(){
        try{
            if(con != null){
                con.close();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    } 

    public static void main(String[] args){
        try{
            if(openConnection()){
                System.out.print("success");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
