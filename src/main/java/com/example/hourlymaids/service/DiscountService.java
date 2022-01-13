package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.DiscountDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.ServiceDomain;

public interface DiscountService {
    ResponseDataAPI getListDiscount(GetListRequest request);

    DiscountDomain getDiscountDetail(String discountId);

    void updateDiscount(String discountId, DiscountDomain domain);

    void createDiscount(DiscountDomain domain);
}
