package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class ServiceDomain {
    private String id;
    private String name;
    private String note;
    private String banner;
    private String status;
    private String price;
    private String createDate;
    private String numTask;
    private String advantage;
    private String introduces;
    private List<DiscountDomain> discounts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getNumTask() {
        return numTask;
    }

    public void setNumTask(String numTask) {
        this.numTask = numTask;
    }

    public String getAdvantage() {
        return advantage;
    }

    public void setAdvantage(String advantage) {
        this.advantage = advantage;
    }

    public String getIntroduces() {
        return introduces;
    }

    public void setIntroduces(String introduces) {
        this.introduces = introduces;
    }

    public List<DiscountDomain> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<DiscountDomain> discounts) {
        this.discounts = discounts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
