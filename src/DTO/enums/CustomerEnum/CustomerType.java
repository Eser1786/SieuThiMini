package DTO.enums.CustomerEnum;

public enum CustomerType {
    REGULAR("REGULAR"), SILVER("SILVER"), GOLD("GOLD"), DIAMOND("DIAMOND");

    private final String value;

    CustomerType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public static CustomerType fromString(String text){
        for(CustomerType type : CustomerType.values()){
            if(type.value.equalsIgnoreCase(text)){
                return type;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại khách hàng: " + text);
    }
}
