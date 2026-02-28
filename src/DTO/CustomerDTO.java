package DTO;

// import DTO.enums.*;
import DTO.enums.CustomerEnum.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerDTO {
    private int id;
    private String code;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private int loyaltyPoints;
    private LocalDateTime createdAt;
    private LocalDateTime lastPurchaseAt;
    private BigDecimal totalSpent;
    private CustomerType type;
    private CustomerStatus status; 

    public int getId(){return id;}
    public String getCode(){return code;}
    public String getFullName(){return fullName;}
    public String getPhone(){return phone;}
    public String getEmail(){return email;}
    public String getAddress(){return address;}
    public int getLoyaltyPoints(){return loyaltyPoints;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getLastPurchaseAt(){return lastPurchaseAt;}
    public BigDecimal getTotalSpent(){return totalSpent;}
    public CustomerType getType(){return type;}
    public CustomerStatus getStatus(){return status;} 

    public void setId(int id){this.id = id;}
    public void setCode(String code){this.code = code;}
    public void setFullName(String fullName){this.fullName = fullName;}
    public void setPhone(String phone){this.phone = phone;}
    public void setEmail(String email){this.email = email;}
    public void setAddress(String address){this.address = address;}
    public void setLoyaltyPoints(int loyaltyPoints){this.loyaltyPoints = loyaltyPoints;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt;}
    public void setLastPurchaseAt(LocalDateTime lastPurchaseAt){this.lastPurchaseAt = lastPurchaseAt;}
    public void setTotalSpent(BigDecimal totalSpent){this.totalSpent = totalSpent;}
    public void setType(CustomerType type){this.type = type;}
    public void setStatus(CustomerStatus status){this.status = status;}

    @Override
    public String toString(){
        return "CustomerDTO{" + 
                "id=" + id +
                ", code='" + code + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
