package com.example.hourlymaids.constant;

public enum LeaveStatus {
    MORNING(3, "Sáng"),
    AFTERNOON(2, "Chiều"),
    ALL(1, "Cả ngày");

    private String value;
    private Integer code;

    LeaveStatus(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static LeaveStatus getStatusByCode(Integer code) {
        for (LeaveStatus status : LeaveStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static LeaveStatus getStatusByValue(String value) {
        for (LeaveStatus status : LeaveStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
