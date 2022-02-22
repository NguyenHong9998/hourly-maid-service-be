package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class CheckPriceResponseDomain {
    private List<PriceListDomain> priceList;
    private String total;
    private List<UserInformDomain> employees;

    public List<PriceListDomain> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceListDomain> priceList) {
        this.priceList = priceList;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<UserInformDomain> getEmployees() {
        return employees;
    }

    public void setEmployees(List<UserInformDomain> employees) {
        this.employees = employees;
    }
}
