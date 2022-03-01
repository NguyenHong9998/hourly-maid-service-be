package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class TaskDetailDomain {
    ClientInformDomain clientInform;
    private String address;
    private String workDate;
    private String note;
    CheckPriceResponseDomain priceList;
    private String numOfEmployee;
    private String startTime;
    private String endTime;
    private List<TaskProgressDomain> progress;

    public ClientInformDomain getClientInform() {
        return clientInform;
    }

    public void setClientInform(ClientInformDomain clientInform) {
        this.clientInform = clientInform;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CheckPriceResponseDomain getPriceList() {
        return priceList;
    }

    public void setPriceList(CheckPriceResponseDomain priceList) {
        this.priceList = priceList;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNumOfEmployee() {
        return numOfEmployee;
    }

    public void setNumOfEmployee(String numOfEmployee) {
        this.numOfEmployee = numOfEmployee;
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

    public List<TaskProgressDomain> getProgress() {
        return progress;
    }

    public void setProgress(List<TaskProgressDomain> progress) {
        this.progress = progress;
    }
}
