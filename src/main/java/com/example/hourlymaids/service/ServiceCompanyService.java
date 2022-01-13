package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.ServiceDomain;

public interface ServiceCompanyService {
    ResponseDataAPI getListService(GetListRequest request);

    ServiceDomain getServiceDetail(String serviceId);

    void updateService(String serviceId, ServiceDomain domain);

    void createService(ServiceDomain domain);
}
