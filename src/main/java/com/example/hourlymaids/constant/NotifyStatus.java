package com.example.hourlymaids.constant;

public enum NotifyStatus {
    PUBLISH(1, "Đã gửi"),
    UN_PUBLISH(2, "Đã tạo"),
    ALL(3, "Tất cả");

    private String value;
    private Integer code;

    NotifyStatus(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static NotifyStatus getStatusByCode(Integer code) {
        for (NotifyStatus status : NotifyStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static NotifyStatus getStatusByValue(String value) {
        for (NotifyStatus status : NotifyStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
