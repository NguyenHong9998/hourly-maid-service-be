package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/check-task")
    public ResponseEntity<Object> getInvoice(@RequestBody CheckPriceDomain domain) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getPriceList(domain)).build());
    }

    @PostMapping("")
    public ResponseEntity<Object> createTask(@RequestBody CreateTaskDomain domain) {
        taskService.createTaskDomain(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("")
    public ResponseEntity<Object> getListTask(@RequestParam(value = "offset", required = false) Integer offset,
                                              @RequestParam(value = "limit", required = false) Integer limit,
                                              @RequestParam(value = "value_search", required = false) String valueSearch,
                                              @RequestParam(value = "type_sort", required = false) String typeSort,
                                              @RequestParam(value = "column_sort", required = false) String columnSort,
                                              @RequestParam(value = "status", required = false) String status) {
        Integer of = (offset == null || offset <= 1) ? 0 : offset - 1;
        Integer lim = (limit == null || limit < 1) ? 10 : limit;
        GetListRequest getListRequest = new GetListRequest();
        getListRequest.setLimit(lim);
        getListRequest.setOffset(of);
        getListRequest.setColumnSort(columnSort);
        getListRequest.setTypeSort(typeSort);
        getListRequest.setValueSearch(valueSearch);
        getListRequest.setStatus(status);
        return ResponseEntity.ok(taskService.getListTask(getListRequest));
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getListTaskOfUser(@RequestParam(value = "offset", required = false) Integer offset,
                                                    @RequestParam(value = "limit", required = false) Integer limit,
                                                    @RequestParam(value = "value_search", required = false) String valueSearch,
                                                    @RequestParam(value = "type_sort", required = false) String typeSort,
                                                    @RequestParam(value = "column_sort", required = false) String columnSort,
                                                    @RequestParam(value = "status", required = false) String status) {
        Integer of = (offset == null || offset <= 1) ? 0 : offset - 1;
        Integer lim = (limit == null || limit < 1) ? 10 : limit;
        GetListRequest getListRequest = new GetListRequest();
        getListRequest.setLimit(lim);
        getListRequest.setOffset(of);
        getListRequest.setColumnSort(columnSort);
        getListRequest.setTypeSort(typeSort);
        getListRequest.setValueSearch(valueSearch);
        getListRequest.setStatus(status);
        return ResponseEntity.ok(taskService.getListTaskOfUser(getListRequest));
    }


    @GetMapping("/employee")
    public ResponseEntity<Object> getListTaskOfEmployee(@RequestParam(value = "offset", required = false) Integer offset,
                                                        @RequestParam(value = "limit", required = false) Integer limit,
                                                        @RequestParam(value = "value_search", required = false) String valueSearch,
                                                        @RequestParam(value = "type_sort", required = false) String typeSort,
                                                        @RequestParam(value = "column_sort", required = false) String columnSort,
                                                        @RequestParam(value = "status", required = false) String status,
                                                        @RequestParam(value = "date", required = false) String date) {
        Integer of = (offset == null || offset <= 1) ? 0 : offset - 1;
        Integer lim = (limit == null || limit < 1) ? 10 : limit;
        GetListRequest getListRequest = new GetListRequest();
        getListRequest.setLimit(lim);
        getListRequest.setOffset(of);
        getListRequest.setColumnSort(columnSort);
        getListRequest.setTypeSort(typeSort);
        getListRequest.setValueSearch(valueSearch);
        getListRequest.setStatus(status);
        return ResponseEntity.ok(taskService.getListTaskOfEmployee(getListRequest, date));
    }

    @GetMapping("/calendar")
    public ResponseEntity<Object> getListWorkDateOfEmployee() {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getListWorkDateOfEmployee()).build());

    }

    @GetMapping("/{task_id}")
    private ResponseEntity<Object> getTaskDetail(@PathVariable("task_id") String taskId) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getTaskDetail(taskId)).build());
    }


    @GetMapping("/check-employee")
    private ResponseEntity<Object> getListUserAvailableWithTaskTime(@RequestParam("start_time") String startTime, @RequestParam("end_time") String endTime) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getListUserAvailableWithTaskTime(startTime, endTime)).build());
    }

    @PutMapping("/assign")
    private ResponseEntity<Object> assignUserToTask(@RequestBody AssignTaskDomain assignTaskDomain) {
        taskService.assignEmployeeToTask(assignTaskDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PutMapping("/update-inform")
    private ResponseEntity<Object> updateTaskInform(@RequestBody UpdateTaskInform updateTaskInform) {
        taskService.upDateTaskInform(updateTaskInform);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PutMapping("/mark-cancel")
    private ResponseEntity<Object> cancelTask(@RequestBody ChangeNotifyStatusDomain cancelTaskInform) {
        taskService.cancelTask(cancelTaskInform);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PutMapping("/mark-done")
    private ResponseEntity<Object> doneTask(@RequestBody ChangeNotifyStatusDomain cancelTaskInform) {
        taskService.doneTask(cancelTaskInform);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/overview")
    private ResponseEntity<Object> getOveriewOfTask(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getTaskOverviewDetail(startDate, endDate)).build());
    }

    @GetMapping("/overview/detail")
    private ResponseEntity<Object> getOveriewDetailOfTask(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getOveriewDetailOfTask(startDate, endDate)).build());
    }

     @GetMapping("/overview/employee")
    private ResponseEntity<Object> getOveriewOfTaskOfEmployee(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getTaskOverviewDetailForEmployee(startDate, endDate)).build());
    }

    @GetMapping("/overview/employee/detail")
    private ResponseEntity<Object> getOveriewDetailOfTaskOfEmployee(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(taskService.getOveriewDetailOfTaskForEmployee(startDate, endDate)).build());
    }
}
