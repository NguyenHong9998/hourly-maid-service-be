package com.example.hourlymaids.constant;

public enum Gender {
    MALE(1, "Nam"),
    FEMALE(2, "Nữ"),
    OTHER(3, "Khác");

    private String value;
    private Integer code;

    Gender(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static Gender getEmployeeStatusByCode(Integer code) {
        for (Gender status : Gender.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return OTHER;
    }

    public static Gender getEmployeeStatusByValue(String value) {
        for (Gender status : Gender.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return OTHER;
    }
}
