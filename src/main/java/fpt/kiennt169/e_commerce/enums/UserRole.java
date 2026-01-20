package fpt.kiennt169.e_commerce.enums;

public enum UserRole {

    ADMIN("ROLE_ADMIN"),

    CUSTOMER("ROLE_CUSTOMER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }

    public String getRoleName() {
        return this.name();
    }
    
    public static UserRole fromAuthority(String authority) {
        for (UserRole role : values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown authority: " + authority);
    }

    @Override
    public String toString() {
        return authority;
    }
}
