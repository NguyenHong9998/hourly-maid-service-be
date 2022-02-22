package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OverviewServiceDomain {
    private String numService;
    private ServiceDomain maxService;
    private ServiceDomain minService;

    List<ServiceOverviewDetailDomain> details;

    public String getNumService() {
        return numService;
    }

    public void setNumService(String numService) {
        this.numService = numService;
    }

    public ServiceDomain getMaxService() {
        return maxService;
    }

    public void setMaxService(ServiceDomain maxService) {
        this.maxService = maxService;
    }

    public ServiceDomain getMinService() {
        return minService;
    }

    public void setMinService(ServiceDomain minService) {
        this.minService = minService;
    }

    public List<ServiceOverviewDetailDomain> getDetails() {
        return details;
    }

    public void setDetails(List<ServiceOverviewDetailDomain> details) {
        this.details = details;
    }
}
