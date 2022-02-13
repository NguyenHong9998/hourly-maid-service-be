package com.example.hourlymaids.constant;

public enum DiscountStatus {
    ACTIVE(1, "Đang diễn ra"),
    INACTIVE(2, "Tạm khoá");

    private String value;
    private Integer code;

    DiscountStatus(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static DiscountStatus getDiscountStatusByCode(Integer code) {
        for (DiscountStatus status : DiscountStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static DiscountStatus getDiscountStatusByValue(String value) {
        for (DiscountStatus status : DiscountStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
