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
    public int getLoyaltyPointes(){return loyaltyPoints;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getLastPurchaseAt(){return lastPurchaseAt;}
    public BigDecimal getTotalSpent(){return totalSpent;}
    public CustomerType getCustomerType(){return type;}
    public CustomerStatus getCustomerStatus(){return status;} 
}
