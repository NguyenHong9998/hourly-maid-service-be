package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DiscountDomain {
    private String id;
    private List<ServiceParamDomain> serviceList;
    private String startTime;
    private String endTime;
    private String note;
    private String title;
    private String banner;
    private String isPublic;
    private String numberService;
    private String salePercentage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ServiceParamDomain> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceParamDomain> serviceList) {
        this.serviceList = serviceList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getPublic() {
        return isPublic;
    }

    public void setPublic(String aPublic) {
        isPublic = aPublic;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public String getNumberService() {
        return numberService;
    }

    public void setNumberService(String numberService) {
        this.numberService = numberService;
    }

    public String getSalePercentage() {
        return salePercentage;
    }

    public void setSalePercentage(String salePercentage) {
        this.salePercentage = salePercentage;
    }
}
