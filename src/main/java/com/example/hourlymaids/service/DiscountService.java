package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;

public interface DiscountService {
    ResponseDataAPI getListDiscount(GetListRequest request);

    DiscountDomain getDiscountDetail(String discountId);

    void updateDiscount(String discountId, DiscountDomain domain);

    void createDiscount(DiscountDomain domain);

    void  changeStatusDiscount(ChangeNotifyStatusDomain domain);

    void deleteDomain(DeleteDomain deleteDomain);

}
