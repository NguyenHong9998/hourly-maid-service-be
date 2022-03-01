package com.example.hourlymaids.constant;

public enum ServiceStatus {
    ACTIVE(1, "Đã cung cấp"),
    INACTIVE(0, "Tạm khoá");

    private String value;
    private Integer code;

    ServiceStatus(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static ServiceStatus getServiceStatusByCode(Integer code) {
        for (ServiceStatus status : ServiceStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static ServiceStatus getEmployeeStatusByValue(String value) {
        for (ServiceStatus status : ServiceStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
