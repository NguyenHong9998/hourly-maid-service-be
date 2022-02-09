package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GetListRequest {
    private Integer limit;
    private Integer offset;
    private String valueSearch;
    private String columnSort;
    private String typeSort;
    private String status;

    public GetListRequest() {

    }

    public GetListRequest(Integer limit, Integer offset, String valueSearch, String columnSort, String typeSort) {
        this.limit = limit;
        this.offset = offset;
        this.valueSearch = valueSearch;
        this.columnSort = columnSort;
        this.typeSort = typeSort;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getValueSearch() {
        return valueSearch;
    }

    public void setValueSearch(String valueSearch) {
        this.valueSearch = valueSearch;
    }

    public String getColumnSort() {
        return columnSort;
    }

    public void setColumnSort(String columnSort) {
        this.columnSort = columnSort;
    }

    public String getTypeSort() {
        return typeSort;
    }

    public void setTypeSort(String typeSort) {
        this.typeSort = typeSort;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
