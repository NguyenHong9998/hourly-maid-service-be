package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;

import java.util.List;

public interface ServiceCompanyService {
    ResponseDataAPI getListService(GetListRequest request);

    ServiceDomain getServiceDetail(String serviceId);

    void updateService(String serviceId, ServiceDomain domain);

    void createService(ServiceDomain domain);

    OverviewServiceDomain getServiceOverviewDetail(String startDate, String endDate);

    List<ServiceOverviewDetailDomain> getOverviewDetailOfService(String startDate, String endDate);

    OverviewServiceDomain getServiceOverviewDetailForEmployee(String startDate, String endDate);

    List<ServiceOverviewDetailDomain> getOverviewDetailOfServiceForEmployee(String startDate, String endDate);

    void changeStatusService(ChangeStatusDomain domain);
}
