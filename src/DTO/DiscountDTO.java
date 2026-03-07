package DTO;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import DTO.enums.DiscountEnum.DiscountType;
import DTO.enums.DiscountEnum.DiscountStatus;
public class DiscountDTO {
    private int id;
    private String name;
    private DiscountType discountType;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private DiscountStatus status;
    private Boolean isAutoApply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
public DiscountDTO() {
    }
public DiscountDTO(int id, String name, DiscountType discountType, BigDecimal value, BigDecimal minOrderAmount, LocalDate startDate, LocalDate endDate, String description, DiscountStatus status, Boolean isAutoApply, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.discountType = discountType;
        this.value = value;
        this.minOrderAmount = minOrderAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.status = status;
        this.isAutoApply = isAutoApply;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }    

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountStatus getStatus() {
        return status;
    }

    public void setStatus(DiscountStatus status) {
        this.status = status;
    }

    public Boolean getIsAutoApply() {
        return isAutoApply;
    }

    public void setIsAutoApply(Boolean isAutoApply) {
        this.isAutoApply = isAutoApply;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getFormattedValue() {
    if (value == null) return "";

    if (discountType == DTO.enums.DiscountEnum.DiscountType.PERCENT) {
        return value + " %";
    } else {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(value) + " VND";
    }
}
    @Override
    public String toString() {
        return "DiscountDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", discountType=" + discountType +
                ", value=" + value +
                ", minOrderAmount=" + minOrderAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", isAutoApply=" + isAutoApply +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
    

