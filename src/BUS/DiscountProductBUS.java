package BUS;

import DAO.DiscountProductDAO;
import DTO.DiscountProductDTO;

import java.util.ArrayList;

public class DiscountProductBUS {

    private DiscountProductDAO dao = new DiscountProductDAO();


    public boolean addDiscountProduct(int discountId, int productId){

        DiscountProductDTO dp = new DiscountProductDTO(discountId, productId);

        return dao.add(dp);
    }


    public boolean removeByDiscount(int discountId){

        return dao.deleteByDiscount(discountId);
    }


    public ArrayList<Integer> getProductsByDiscount(int discountId){

        return dao.getProductsByDiscount(discountId);
    }


    public ArrayList<Integer> getDiscountByProduct(int productId){

        return dao.getDiscountByProduct(productId);
    }

}