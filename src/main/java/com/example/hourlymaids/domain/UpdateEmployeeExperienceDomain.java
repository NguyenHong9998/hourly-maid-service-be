package com.example.hourlymaids.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class UpdateEmployeeExperienceDomain {
    private List<EmployeeServiceDomain> employeeService;
    private String employeeId;

    public List<EmployeeServiceDomain> getEmployeeService() {
        return employeeService;
    }

    public void setEmployeeService(List<EmployeeServiceDomain> employeeService) {
        this.employeeService = employeeService;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
