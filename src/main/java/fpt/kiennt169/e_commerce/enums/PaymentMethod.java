package fpt.kiennt169.e_commerce.enums;

public enum PaymentMethod {

    COD("Cash on Delivery"),

    BANK_TRANSFER("Bank Transfer");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMethodName() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.name();
    }
}
