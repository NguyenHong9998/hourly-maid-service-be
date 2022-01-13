package com.example.hourlymaids.service;

import com.example.hourlymaids.domain.EmployeeServiceDomain;

import java.util.List;

public interface EmployeeServiceService {

    List<EmployeeServiceDomain> getServiceListOfEmployee(String userId);

    List<EmployeeServiceDomain> getListUserOfServiceId(String serviceId);

    void updateListServiceOfEmployee(String employeeId, List<EmployeeServiceDomain> domains);

}
