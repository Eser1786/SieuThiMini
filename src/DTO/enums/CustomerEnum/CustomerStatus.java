package DTO.enums.CustomerEnum;

public enum CustomerStatus {
    ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), BLOCKED("BLOCKED");

    private final String value;
    
    CustomerStatus(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static CustomerStatus fromString(String text){
        for(CustomerStatus status : CustomerStatus.values()){
            if(status.value.equalsIgnoreCase(text)){
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy status: " + text);
    }
}
