package com.example.hourlymaids.constant;

public enum ColumnSortLeaveDate {
    NAME("u.fullName", "NAME"),
    LEAVE_DATE("leaveDate", "LEAVE_DATE"),
    START("start", "START"),
    END("end", "END"),
    CREATED_DATE("createdDate", "CREATED_DATE");

    private String value;
    private String name;

    ColumnSortLeaveDate(String value, String name) {
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
