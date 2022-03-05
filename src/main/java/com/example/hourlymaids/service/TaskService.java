package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;

import java.util.List;

public interface TaskService {
    CheckPriceResponseDomain getPriceList(CheckPriceDomain domain);

    void createTaskDomain(CreateTaskDomain domain);

    ResponseDataAPI getListTask(GetListRequest request);

    ResponseDataAPI getListTaskOfUser(GetListRequest request);


    TaskDetailDomain getTaskDetail(String taskId);

    List<UserInformDomain> getListUserAvailableWithTaskTime(String startTime, String endTime, String serviceId, String taskId);

    void assignEmployeeToTask(AssignTaskDomain assignTaskDomain);

    void upDateTaskInform(UpdateTaskInform updateTaskInform);

    void cancelTask(ChangeNotifyStatusDomain cancelTaskInform);

    void doneTask(ChangeNotifyStatusDomain doneDomain);

    OverviewTaskDomain getTaskOverviewDetail(String statDate, String endDate);

    List<ItemOnDateDomain> getOveriewDetailOfTask(String statDate, String endDate);

    OverviewTaskDomain getTaskOverviewDetailForEmployee(String statDate, String endDate);

    List<ItemOnDateDomain> getOveriewDetailOfTaskForEmployee(String statDate, String endDate);

    ResponseDataAPI getListTaskOfEmployee(GetListRequest getListRequest, String date);

    List<String> getListWorkDateOfEmployee();
}
