package com.example.hourlymaids.constant;

public enum ColumnSortTask {
    NAME("c.fullName", "NAME"),
    WORK_DATE("workDate", "WORK_DATE"),
    START_TIME("startTime", "START_TIME"),
    END_TIME("endTime", "END_TIME"),
    CREATED_DATE("createdDate", "CREATE_DATE"),
    IS_PAID("paid", "PAID");

    private String value;
    private String name;

    ColumnSortTask(String value, String name) {
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
