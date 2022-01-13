package com.example.hourlymaids.constant;

public enum AddressType {
    HOME_ADDRESS(1, "Địa chỉ nhà"),
    COMPANY_ADDRESS(2, "Địa chỉ công ty"),
    HOTEL_ADDRESS(3, "Địa chỉ nhà nghỉ, khách sạn"),
    SITE_ADDRESS(4, "Địa chỉ công trường");

    private Integer code;
    private String name;

    AddressType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }
}
