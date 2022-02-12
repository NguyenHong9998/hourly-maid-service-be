package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class OverviewFeedbackDomain {
    private String numUser;
    private List<FeedbackDetailDomain> detail;

    public String getNumUser() {
        return numUser;
    }

    public void setNumUser(String numUser) {
        this.numUser = numUser;
    }

    public List<FeedbackDetailDomain> getDetail() {
        return detail;
    }

    public void setDetail(List<FeedbackDetailDomain> detail) {
        this.detail = detail;
    }
}
