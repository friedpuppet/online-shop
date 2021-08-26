package ua.yelisieiev.entity;

public enum Role {
    ADMIN("ADMIN"),
    GUEST("GUEST"),
    CUSTOMER("CUSTOMER");

    private String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Role of(String roleName) {
        if ("ADMIN".equals(roleName)) {
            return ADMIN;
        }
        if ("GUEST".equals(roleName)) {
            return GUEST;
        }
        if ("CUSTOMER".equals(roleName)) {
            return CUSTOMER;
        }
        throw new RuntimeException("No such role " + roleName);
    }
}
