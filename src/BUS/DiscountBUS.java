package BUS;
import DTO.DiscountDTO;
import java.math.BigDecimal;

import java.util.ArrayList;


import DAO.DiscountDAO;
public class DiscountBUS {
    private DiscountDAO discountDAO;
    public DiscountBUS() {
        discountDAO = new DiscountDAO();
    }
    public ArrayList<DiscountDTO> getAllDiscounts() {
        return discountDAO.getAllDiscounts();
    }
    public boolean addDiscount(DiscountDTO discount) {
        if(discount.getName() == null || discount.getName().isEmpty()){
            System.out.println("Tên khuyến mãi không được để trống.");
            return false;
        }
        if(discount.getValue() == null || discount.getValue().compareTo(BigDecimal.ZERO) <= 0){
            System.out.println("Giá trị khuyến mãi phải lớn hơn 0.");
            return false;
        }
        if(discount.getStartDate() == null || discount.getEndDate() == null || discount.getStartDate().isAfter(discount.getEndDate())){
            System.out.println("Ngày bắt đầu phải trước ngày kết thúc.");
            return false;
        }
        return discountDAO.addDiscount(discount);
    }
}
