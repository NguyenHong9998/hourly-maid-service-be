package com.example.hourlymaids.constant;

public enum ColumnSortDiscount {
    START_TIME("startTime", "START_TIME"),
    END_TIME("endTime", "END_TIME"),
    SALE_PERCENTAGE("salePercentage", "SALE_PERCENTAGE"),
    TITLE("title", "TITLE");

    private String value;
    private String name;

    ColumnSortDiscount(String value, String name) {
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
