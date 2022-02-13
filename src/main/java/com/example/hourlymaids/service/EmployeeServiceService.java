package com.example.hourlymaids.service;

import com.example.hourlymaids.domain.EmployeeServiceDomain;
import com.example.hourlymaids.domain.GetListDiscountOfService;

import java.util.List;

public interface EmployeeServiceService {

    List<EmployeeServiceDomain> getServiceListOfEmployee(String userId);

    List<EmployeeServiceDomain> getListUserOfServiceId(String serviceId, String typeSort, String columnSort);

    void updateListServiceOfEmployee(String employeeId, List<EmployeeServiceDomain> domains);

    List<GetListDiscountOfService> getListDiscountOfService(String serviceId, String typeSort, String columnSort);
}
