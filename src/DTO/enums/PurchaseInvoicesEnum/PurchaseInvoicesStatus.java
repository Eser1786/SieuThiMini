package DTO.enums.PurchaseInvoicesEnum;

public enum PurchaseInvoicesStatus {
    PENDING("PENDING"), RECEIVED("RECEIVED"), CANCELLED("CANCELLED");

    private final String value;

    PurchaseInvoicesStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PurchaseInvoicesStatus fromString(String text) {
        for (PurchaseInvoicesStatus status : PurchaseInvoicesStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return null;
    }
}