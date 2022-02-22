package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class UserOverviewDomain {
    private UserInformDomain maxUser;
    private UserInformDomain minUser;
    private String numOfUser;

    List<UserInformDomain> details;

    public UserInformDomain getMaxUser() {
        return maxUser;
    }

    public void setMaxUser(UserInformDomain maxUser) {
        this.maxUser = maxUser;
    }

    public UserInformDomain getMinUser() {
        return minUser;
    }

    public void setMinUser(UserInformDomain minUser) {
        this.minUser = minUser;
    }

    public String getNumOfUser() {
        return numOfUser;
    }

    public void setNumOfUser(String numOfUser) {
        this.numOfUser = numOfUser;
    }

    public List<UserInformDomain> getDetails() {
        return details;
    }

    public void setDetails(List<UserInformDomain> details) {
        this.details = details;
    }
}
