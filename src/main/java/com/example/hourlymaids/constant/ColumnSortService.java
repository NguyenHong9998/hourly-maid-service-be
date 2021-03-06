package com.example.hourlymaids.constant;

public enum ColumnSortService {
    NAME("serviceName", "NAME"),
    PRICE("price", "PRICE"),
    NOTE("note", "NOTE"),
    CREATED_DATE("createdDate", "CREATE_DATE");

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
