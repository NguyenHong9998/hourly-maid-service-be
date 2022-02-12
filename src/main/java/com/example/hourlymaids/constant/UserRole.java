package com.example.hourlymaids.constant;

public enum UserRole {
    MANAGER(1, "Quản lý", "MANAGER"),
    EMPLOYEE(2, "Nhân viên", "EMPLOYEE"),
    ALL(3, "Tất cả", "ALL");

    private String value;
    private Integer code;
    private String name;

    UserRole(Integer code, String value, String name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static UserRole getRoleByCode(Integer code) {
        for (UserRole status : UserRole.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static UserRole getRoleByValue(String value) {
        for (UserRole status : UserRole.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

      public static UserRole getRoleByName(String name) {
        for (UserRole status : UserRole.values()) {
            if (status.getName().equals(name)) {
                return status;
            }
        }
        return null;
    }
}
