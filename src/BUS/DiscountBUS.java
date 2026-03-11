package BUS;

import DTO.DiscountDTO;
import DAO.DiscountDAO;
import DAO.DiscountProductDAO;
import DTO.DiscountProductDTO;
import BUS.DiscountProductBUS;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import DTO.enums.DiscountEnum.DiscountStatus;
import DTO.enums.DiscountEnum.DiscountType;
import DTO.ProductDTO;

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
        String status,
        String start,
        String end,
        String minOrder,
        Integer productId
) {
        return addDiscount(name, desc, value, type, status, start, end, minOrder, productId, null);
    }

    public String addDiscount(
        String name,
        String desc,
        String value,
        String type,
        String status,
        String start,
        String end,
        String minOrder,
        Integer productId,
        String code
){

    

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


    

    DiscountType discountType;

    try{
        discountType = DiscountType.valueOf(type.toUpperCase());
    }catch(Exception e){
        return "Loại giảm chỉ được là PERCENT hoặc FIXED";
    }

    
    if (discountType == DiscountType.PERCENT && productId != null)
        return "Khŋyến mãi PERCENT áp dụng cho toàn hóa đơn, không gắn với sản phẩm cụ thể";

    if (discountType == DiscountType.FIXED && productId == null)
        return "Khŋyến mãi FIXED phải chọn sản phẩm áp dụng";

    DiscountStatus discountStatus;

try{
    discountStatus = DiscountStatus.valueOf(status.toUpperCase());
}catch(Exception e){
    return "Trạng thái chỉ được là ACTIVE hoặc EXPIRED";
}
    

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


    

    BigDecimal min = BigDecimal.ZERO;

    if(minOrder != null && !minOrder.trim().isEmpty()){

        try{
            min = new BigDecimal(minOrder);
        }catch(NumberFormatException e){
            return "Min order phải là số";
        }

    }

    if(min.compareTo(BigDecimal.ZERO) < 0)
        return "Giá trị đơn hàng tối thiểu (Min order) không được là số âm";


    

    if (discountType == DiscountType.FIXED && productId != null) {
        ProductBUS productBUS = new ProductBUS();
        ProductDTO product = null;
        for (ProductDTO p : productBUS.getAllProducts()) {
            if (p.getId() == productId) { product = p; break; }
        }
        if (product != null && product.getSellingPrice() != null &&
                val.compareTo(product.getSellingPrice()) > 0) {
            return "Không thể tạo khŋyến mãi này:\n"
                 + "• Tên khŋyến mãi : " + name + "\n"
                 + "• Sản phẩm chọn  : " + product.getName() + "\n"
                 + "• Giá tri giảm    : " + String.format("%,.0f VNĐ", val)
                 + "   >   Giá bán : " + String.format("%,.0f VNĐ", product.getSellingPrice()) + "\n"
                 + "Lý do: giá trị giảm không được vượt quá giá bán của sản phẩm.";
        }
    }


    

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
    if (code != null && !code.trim().isEmpty()) d.setDiscountCode(code.trim().toUpperCase());
    int discountId = discountDAO.addDiscount(d);

if(discountId <= 0)
    return "Không thể thêm khuyến mãi";

if(discountType == DiscountType.FIXED){

    DiscountProductBUS dp = new DiscountProductBUS();
    dp.addDiscountProduct(discountId, productId);
}

return "SUCCESS";

    }

    public DiscountDTO getDiscountByCode(String code) {
        return discountDAO.getDiscountByCode(code);
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
public String updateDiscount(
        int id,
        String name,
        String description,
        double value,
        String type,
        String startDate,
        String endDate,
        double minOrder,
        String status,
        Integer productId
){
    
    if (minOrder < 0)
        return "Giá trị đơn hàng tối thiểu (Min order) không được là số âm";

    
    if ("FIXED".equals(type) && productId != null) {
        ProductBUS productBUS = new ProductBUS();
        ProductDTO product = null;
        for (ProductDTO p : productBUS.getAllProducts()) {
            if (p.getId() == productId) { product = p; break; }
        }
        if (product != null && product.getSellingPrice() != null) {
            BigDecimal val = BigDecimal.valueOf(value);
            if (val.compareTo(product.getSellingPrice()) > 0) {
                return "Không thể cập nhật khŋyến mãi này:\n"
                     + "• Tên khŋyến mãi : " + name + "\n"
                     + "• Sản phẩm chọn  : " + product.getName() + "\n"
                     + "• Giá trị giảm   : " + String.format("%,.0f VNĐ", val)
                     + "   >   Giá bán : " + String.format("%,.0f VNĐ", product.getSellingPrice()) + "\n"
                     + "Lý do: giá trị giảm không được vượt quá giá bán của sản phẩm.";
            }
        }
    }

    boolean result = discountDAO.updateDiscount(
            id,name,description,value,type,startDate,endDate,minOrder,status
    );

    if(!result) return "Cập nhật thất bại";

    if(type.equals("FIXED")){

        DiscountProductDAO dpDAO = new DiscountProductDAO();

        
        dpDAO.deleteByDiscountId(id);

        
        if(productId != null){

            DiscountProductDTO dp = new DiscountProductDTO();
            dp.setDiscountId(id);
            dp.setProductId(productId);

            dpDAO.add(dp);
        }
    }

    return "SUCCESS";
}
}
