package com.example.hourlymaids.constant;

public enum ColumnSortEmployeeService {
    NAME("fullName", "NAME"),
    LEVEL("level", "LEVEL");

    private String value;
    private String name;

    ColumnSortEmployeeService(String value, String name) {
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
