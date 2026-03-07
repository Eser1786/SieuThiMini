package BUS;

import DTO.DiscountDTO;
import DAO.DiscountDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import DTO.enums.DiscountEnum.DiscountStatus;
import DTO.enums.DiscountEnum.DiscountType;

public class DiscountBUS {

    private DiscountDAO discountDAO;

    public DiscountBUS() {
        discountDAO = new DiscountDAO();
    }

    public ArrayList<DiscountDTO> getAllDiscounts() {
        return discountDAO.getAllDiscounts();
    }

    public String addDiscount(
        String name,
        String desc,
        String value,
        String type,
        String status, // thêm tham số trạng thái
        String start,
        String end,
        String minOrder
){

    // ===== VALIDATE =====

    if(name == null || name.trim().isEmpty())
        return "Tên khuyến mãi không được để trống";

    if(value == null || value.trim().isEmpty())
        return "Giá trị giảm không được để trống";

    BigDecimal val;

    try{
        val = new BigDecimal(value);
    }catch(NumberFormatException e){
        return "Giá trị giảm phải là số";
    }

    if(val.compareTo(BigDecimal.ZERO) <= 0)
        return "Giá trị giảm phải > 0";


    // ===== TYPE =====

    DiscountType discountType;

    try{
        discountType = DiscountType.valueOf(type.toUpperCase());
    }catch(Exception e){
        return "Loại giảm chỉ được là PERCENT hoặc FIXED";
    }

    DiscountStatus discountStatus;

try{
    discountStatus = DiscountStatus.valueOf(status.toUpperCase());
}catch(Exception e){
    return "Trạng thái chỉ được là ACTIVE hoặc EXPIRED";
}
    // ===== DATE =====

    LocalDate startDate;
    LocalDate endDate;

    try{
        startDate = LocalDate.parse(start);
        endDate = LocalDate.parse(end);
    }catch(Exception e){
        return "Ngày không đúng định dạng yyyy-MM-dd";
    }

    if(endDate.isBefore(startDate))
        return "Ngày kết thúc phải sau ngày bắt đầu";


    // ===== MIN ORDER =====

    BigDecimal min = BigDecimal.ZERO;

    if(minOrder != null && !minOrder.trim().isEmpty()){

        try{
            min = new BigDecimal(minOrder);
        }catch(NumberFormatException e){
            return "Min order phải là số";
        }

    }


    // ===== CREATE DTO =====

    DiscountDTO d = new DiscountDTO();

    d.setName(name);
    d.setDescription(desc);
    d.setValue(val);
    d.setDiscountType(discountType);
    d.setStatus(discountStatus);
    d.setStartDate(startDate);
    d.setEndDate(endDate);
    d.setMinOrderAmount(min);
    d.setIsAutoApply(false);
    d.setCreatedAt(LocalDateTime.now());
d.setUpdatedAt(LocalDateTime.now());
    boolean result = discountDAO.addDiscount(d);

    if(result)
        return "SUCCESS";

    return "Không thể thêm khuyến mãi";
}

    public DiscountDTO getDiscountById(int id){

        for(DiscountDTO d : getAllDiscounts()){

            if(d.getId() == id)
                return d;

        }

        return null;
    }
    public String deleteDiscount(int id){

    if(id <= 0)
        return "ID không hợp lệ";

    boolean result = discountDAO.deleteDiscount(id);

    if(result)
        return "SUCCESS";

    return "Không thể xóa khuyến mãi";
}
public boolean updateDiscount(
        int id,
        String name,
        String description,
        double value,
        String type,
        String startDate,
        String endDate,
        double minOrder,
        String status

){
    return discountDAO.updateDiscount(
            id,name,description,value,type,startDate,endDate,minOrder,status
    );
}
}