package DTO;

import DTO.enums.SaleEnum.*;

import java.time.LocalDate;
import java.math.BigDecimal;


public class SaleDTO {

    private int saleID;
    private String saleCode;
    private LocalDate saleDate;

    // customer
    private int customerID;
    private String customerCode;
    private String customerName;

    // employee
    private int employeeID;
    private String employeeCode;
    private String employeeName;


    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private SaleStatus status;
    private SalePaymentMethod paymentMethod;
    private BigDecimal totalAmount;
    private String note;


    //GETTER
    public int getSaleID(){return saleID;}
    public String getSaleCode(){return saleCode;}
    public LocalDate getSaleDate(){return saleDate;}
    
    public int getCustomerID(){return customerID;}
    public String getCustomerCode(){return customerCode;}
    public String getCustomerName(){return customerName;}

    public int getEmployeeID(){return employeeID;}
    public String getEmployeeCode(){return employeeCode;}
    public String getEmployeeName(){return employeeName;}

    public BigDecimal getSubTotal(){return subTotal;}
    public BigDecimal getDiscountAmount(){return discountAmount;}
    public SaleStatus getSaleStatus(){return status;}
    public SalePaymentMethod getSalePaymentMethod(){return paymentMethod;}
    public BigDecimal getTotalAmount(){return totalAmount;}
    public String getNote(){return note;}

    
    //SETTER 
    public void setSaleID(int saleID){this.saleID = saleID;}
    public void setSaleCode(String saleCode){this.saleCode = saleCode;}
    public void setSaleDate(LocalDate saleDate){this.saleDate = saleDate;}
    
    public void setCustomerID(int customerID){this.customerID = customerID;}
    public void setCustomerCode(String customerCode){this.customerCode = customerCode;}
    public void setCustomerName(String customerName){this.customerName = customerName;}

    public void setEmployeeID(int employeeID){this.employeeID = employeeID;}
    public void setEmployeeCode(String employeeCode){this.employeeCode = employeeCode;}
    public void setEmployeeName(String employeeName){this.employeeName = employeeName;}

    public void setSubTotal(BigDecimal subTotal){this.subTotal = subTotal;}
    public void setDiscountAmount(BigDecimal discountAmount){this.discountAmount = discountAmount;}
    public void setSaleStatus(SaleStatus status){this.status = status;}
    public void setPaymentMethod(SalePaymentMethod paymentMethod){this.paymentMethod = paymentMethod;}
    public void setTotalAmount(BigDecimal totalAmount){this.totalAmount = totalAmount;}
    public void setNote(String note){this.note = note;}

    public String toString(){
        return "SaleDTO{" + 
                "sale id:" + saleID + 
                ", sale code:'" + saleCode + '\'' + 
                ", sale date:'" + saleDate + '\'' + 
                ", customer id:" + customerID +
                ", customer code:'" + customerCode + '\''+
                ", customer name:'" + customerName + '\'' + 
                ", employee id:" + employeeID +
                ", employee code:'" + employeeCode + '\'' +
                ", employee name:'" + employeeName + '\'' +
                ", sub total:'" + subTotal + '\'' + 
                ", discount amount:'" + discountAmount + '\'' +
                ", sale status:'" + status + '\'' +
                ", payment method:'" + paymentMethod + '\'' +
                ", total amount:'" + totalAmount + '\'' +
                ", note:'" + note + '\'' + "}";
    }
}
