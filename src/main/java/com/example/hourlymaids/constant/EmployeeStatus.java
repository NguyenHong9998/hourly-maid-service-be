package com.example.hourlymaids.constant;

public enum EmployeeStatus {
    ACTIVE(1, "Hoạt động"),
    INACTIVE(2, "Tạm khoá");

    private String value;
    private Integer code;

    EmployeeStatus(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static EmployeeStatus getEmployeeStatusByCode(Integer code) {
        for (EmployeeStatus status : EmployeeStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static EmployeeStatus getEmployeeStatusByValue(String value) {
        for (EmployeeStatus status : EmployeeStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
