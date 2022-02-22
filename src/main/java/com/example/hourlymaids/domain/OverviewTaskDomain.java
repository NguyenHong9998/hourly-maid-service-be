package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class OverviewTaskDomain {
    private String numCreate;
    private String percentCreate;
    private String numCancel;
    private String percentCancel;
    private String numDone;
    private String percentDone;

    List<ItemOnDateDomain> details;

    public String getNumCreate() {
        return numCreate;
    }

    public void setNumCreate(String numCreate) {
        this.numCreate = numCreate;
    }

    public String getPercentCreate() {
        return percentCreate;
    }

    public void setPercentCreate(String percentCreate) {
        this.percentCreate = percentCreate;
    }

    public String getNumCancel() {
        return numCancel;
    }

    public void setNumCancel(String numCancel) {
        this.numCancel = numCancel;
    }

    public String getPercentCancel() {
        return percentCancel;
    }

    public void setPercentCancel(String percentCancel) {
        this.percentCancel = percentCancel;
    }

    public String getNumDone() {
        return numDone;
    }

    public void setNumDone(String numDone) {
        this.numDone = numDone;
    }

    public String getPercentDone() {
        return percentDone;
    }

    public void setPercentDone(String percentDone) {
        this.percentDone = percentDone;
    }

    public List<ItemOnDateDomain> getDetails() {
        return details;
    }

    public void setDetails(List<ItemOnDateDomain> details) {
        this.details = details;
    }
}
