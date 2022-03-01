package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class OverviewOfEmployeeDomain {
    private String totalStar;
    private String totalFeedUser;
    private String position;

    List<ItemOnDateDomain> details;

    public String getTotalStar() {
        return totalStar;
    }

    public void setTotalStar(String totalStar) {
        this.totalStar = totalStar;
    }

    public String getTotalFeedUser() {
        return totalFeedUser;
    }

    public void setTotalFeedUser(String totalFeedUser) {
        this.totalFeedUser = totalFeedUser;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<ItemOnDateDomain> getDetails() {
        return details;
    }

    public void setDetails(List<ItemOnDateDomain> details) {
        this.details = details;
    }
}
