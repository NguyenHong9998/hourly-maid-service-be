package com.example.hourlymaids.constant;

public enum ColumnSortUser {
    NAME("fullName", "NAME"),
    EMAIL("email", "EMAIL"),
    STATUS("status", "STATUS"),
    ROLE("r.name", "role"),
    CREATED_DATE("createdDate", "CREATED_DATE");

    private String value;
    private String name;

    ColumnSortUser(String value, String name) {
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
