package com.example.hourlymaids.domain;

import java.util.Date;

public class MapDomain {
    private String key;
    private Date value;

    public MapDomain() {
    }

    public MapDomain(String key, Date value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }
}
