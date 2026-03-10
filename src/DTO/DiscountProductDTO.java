

package DTO;

public class DiscountProductDTO {

    private int discountId;
    private int productId;

    public DiscountProductDTO() {}

    public DiscountProductDTO(int discountId, int productId) {
        this.discountId = discountId;
        this.productId = productId;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
