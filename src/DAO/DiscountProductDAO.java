package DAO;

import DTO.DiscountProductDTO;
import DAO.DBConnection;

import java.sql.*;
import java.util.ArrayList;

public class DiscountProductDAO {

    public boolean add(DiscountProductDTO dp) {

        String sql = "INSERT INTO discount_products(discount_id, product_id) VALUES(?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dp.getDiscountId());
            ps.setInt(2, dp.getProductId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean deleteByDiscount(int discountId){

        String sql = "DELETE FROM discount_products WHERE discount_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, discountId);

            return ps.executeUpdate() > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }


    public ArrayList<Integer> getProductsByDiscount(int discountId){

        ArrayList<Integer> list = new ArrayList<>();

        String sql = "SELECT product_id FROM discount_products WHERE discount_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, discountId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(rs.getInt("product_id"));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }


    public ArrayList<Integer> getDiscountByProduct(int productId){

        ArrayList<Integer> list = new ArrayList<>();

        String sql = "SELECT discount_id FROM discount_products WHERE product_id = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(rs.getInt("discount_id"));
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

}