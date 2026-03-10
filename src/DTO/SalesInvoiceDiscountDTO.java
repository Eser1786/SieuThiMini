package DTO;

public class SalesInvoiceDiscountDTO {
    
    private Long id;
    private Long discountId;
    private Long invoiceId;

    public SalesInvoiceDiscountDTO() {}

    public SalesInvoiceDiscountDTO(Long id, Long discountId, Long invoiceId) {
        this.id = id;
        this.discountId = discountId;
        this.invoiceId = invoiceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Long invoiceId) {
        this.invoiceId = invoiceId;
    }
}