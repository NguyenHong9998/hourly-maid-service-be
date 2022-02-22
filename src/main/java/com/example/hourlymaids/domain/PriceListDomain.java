package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PriceListDomain {
    private String serviceName;
    private String servicePrice;
    private String discountApply;
    private String percentApply;
    private String hours;
    private String price;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getDiscountApply() {
        return discountApply;
    }

    public void setDiscountApply(String discountApply) {
        this.discountApply = discountApply;
    }

    public String getPercentApply() {
        return percentApply;
    }

    public void setPercentApply(String percentApply) {
        this.percentApply = percentApply;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
