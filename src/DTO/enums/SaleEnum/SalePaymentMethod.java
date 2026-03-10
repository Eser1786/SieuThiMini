package DTO.enums.SaleEnum;

// import DTO.enums.CustomerEnum.SalePaymentMethod;

public enum SalePaymentMethod {
    CASH("CASH"), CARD("CARD"), TRANSFER("TRANSFER");

    private final String value;
    
    SalePaymentMethod(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static SalePaymentMethod fromString(String text){
        for(SalePaymentMethod status : SalePaymentMethod.values()){
            if(status.value.equalsIgnoreCase(text)){
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy status: " + text);
    }
}
