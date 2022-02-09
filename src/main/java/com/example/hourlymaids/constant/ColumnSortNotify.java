package com.example.hourlymaids.constant;

public enum ColumnSortNotify {
    TITLE("title","TITLE"),

    CONTENT("content","CONTENT"),

    STATUS("status", "STATUS"),

    TYPE("topic", "TYPE"),

    PUBLISH_DATE("publishDate", "PUBLISH_DATE"),

    CREATE_DATE("createdDate", "CREATE_DATE")

    ;

    private String value;
    private String name;

    ColumnSortNotify(String value, String name) {
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
