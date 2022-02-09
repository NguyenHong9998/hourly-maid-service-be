package com.example.hourlymaids.constant;

public enum NotifyTopic {
    TO_USER(1, "Khách hàng"),
    TO_EMPLOYEE(2, "Nhân viên"),
    ALL(3, "Tất cả");

    private String value;
    private Integer code;

    NotifyTopic(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static NotifyTopic getTopicByCode(Integer code) {
        for (NotifyTopic status : NotifyTopic.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static NotifyTopic getToppicByValue(String value) {
        for (NotifyTopic status : NotifyTopic.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
