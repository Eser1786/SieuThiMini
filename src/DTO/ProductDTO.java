package DTO;

public class ProductDTO {
    private int id;
    private String code;
    private String name;
    private String description;
    private SupplierDTO supplier;
    private CategoryDTO category;
    private java.math.BigDecimal costPrice;
    private java.math.BigDecimal sellingPrice;
    private long totalQuantity; // dynamic but kept for convenience
    private long minStockLevel;
    private String madeIn;
    private java.time.LocalDate productionDate;
    private java.time.LocalDate expireDate;
    private String position;
    private String unit;
    private String status;        // store enum as string
    private boolean isVisible;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public SupplierDTO getSupplier() { return supplier; }
    public void setSupplier(SupplierDTO supplier) { this.supplier = supplier; }
    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
    public java.math.BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(java.math.BigDecimal costPrice) { this.costPrice = costPrice; }
    public java.math.BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(java.math.BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(long totalQuantity) { this.totalQuantity = totalQuantity; }
    public long getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(long minStockLevel) { this.minStockLevel = minStockLevel; }
    public String getMadeIn() { return madeIn; }
    public void setMadeIn(String madeIn) { this.madeIn = madeIn; }
    public java.time.LocalDate getProductionDate() { return productionDate; }
    public void setProductionDate(java.time.LocalDate productionDate) { this.productionDate = productionDate; }
    public java.time.LocalDate getExpireDate() { return expireDate; }
    public void setExpireDate(java.time.LocalDate expireDate) { this.expireDate = expireDate; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean getIsVisible() { return isVisible; }
    public void setIsVisible(boolean isVisible) { this.isVisible = isVisible; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", category=" + (category != null ? category.getName() : "null") +
                ", supplier=" + (supplier != null ? supplier.getName() : "null") +
                ", status='" + status + '\'' +
                '}';
    }
}
