package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ServiceOverviewDetailDomain {
    List<ItemOnDateDomain> details;
    String service;

    public List<ItemOnDateDomain> getDetails() {
        return details;
    }

    public void setDetails(List<ItemOnDateDomain> details) {
        this.details = details;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
