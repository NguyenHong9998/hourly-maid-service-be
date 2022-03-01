package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.ChangeNotifyStatusDomain;
import com.example.hourlymaids.domain.DeleteDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.NotifyDomain;

public interface NotifyService {
    void createNotify(NotifyDomain domain);

    ResponseDataAPI getListNotify(GetListRequest request);

    ResponseDataAPI getListNotifyForUser(GetListRequest request);

    void changeStatusNotify(ChangeNotifyStatusDomain domain);

    NotifyDomain getDetailNotify(String id);

    void editNotify(NotifyDomain domain);

    void deleteNotify(DeleteDomain deleteDomain);

}
