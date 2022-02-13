package com.example.hourlymaids.constant;

public enum ColumnSortDiscountService {
    TITLE("title","TITLE"),

    START_TIME("startTime","START_TIME"),

    END_TIME("endTime", "END_TIME"),
    PERCENT("salePercentage", "PERCENT");

    private String value;
    private String name;

    ColumnSortDiscountService(String value, String name) {
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
