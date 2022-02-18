package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.DeleteDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.LeaveDateDomain;

import java.util.List;

public interface LeaveDateService {
    void createLeaveDate(LeaveDateDomain leaveDateDomain);

    void editLeaveDate(LeaveDateDomain leaveDateDomain);

    ResponseDataAPI getListLeaveDate(GetListRequest request, String leaveDate);

    List<String> getListDateHasLeaveDate();

    LeaveDateDomain getLeaveDateInform(String leaveId);

    void deleteLeaveDate(DeleteDomain domain);
}
