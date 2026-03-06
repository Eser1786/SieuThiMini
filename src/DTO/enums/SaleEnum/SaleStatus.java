package DTO.enums.SaleEnum;

// import DTO.enums.CustomerEnum.SaleStatus;

public enum SaleStatus {
    COMPLETED("COMPLETED"), CANCELLED("CANCELLED");

    private final String value;
    
    SaleStatus(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static SaleStatus fromString(String text){
        for(SaleStatus status : SaleStatus.values()){
            if(status.value.equalsIgnoreCase(text)){
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy status: " + text);
    }
}
