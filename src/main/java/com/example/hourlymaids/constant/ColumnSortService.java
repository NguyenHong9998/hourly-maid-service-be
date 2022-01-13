package com.example.hourlymaids.constant;

public enum ColumnSortService {
    NAME("serviceName", "NAME"),
    PRICE("price", "PRICE");

    private String value;
    private String name;

    ColumnSortService(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
