package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.ChangeStatusDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.ServiceDomain;
import com.example.hourlymaids.service.EmployeeServiceService;
import com.example.hourlymaids.service.ServiceCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/service")
public class ServiceCompanyController {

    @Autowired
    private ServiceCompanyService service;

    @Autowired
    private EmployeeServiceService employeeServiceService;

    @GetMapping("")
    public ResponseEntity<ResponseDataAPI> getListService(@RequestParam(value = "offset", required = false) Integer offset,
                                                          @RequestParam(value = "limit", required = false) Integer limit,
                                                          @RequestParam(value = "value_search", required = false) String valueSearch,
                                                          @RequestParam(value = "type_sort", required = false) String typeSort,
                                                          @RequestParam(value = "column_sort", required = false) String columnSort) {
        GetListRequest request = new GetListRequest(limit, offset, valueSearch, columnSort, typeSort);
        ResponseDataAPI dataAPI = service.getListService(request);
        return ResponseEntity.ok(dataAPI);
    }

    @GetMapping("/{service_id}")
    public ResponseEntity<ResponseDataAPI> getServiceDetail(@PathVariable("service_id") String serviceId) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(service.getServiceDetail(serviceId)).build());
    }

    @PutMapping("/{service_id}")
    public ResponseEntity<ResponseDataAPI> editServiceDetail(@PathVariable("service_id") String serviceId, @RequestBody ServiceDomain serviceDomain) {
        service.updateService(serviceId, serviceDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseDataAPI> createService(@RequestBody ServiceDomain domain) {
        service.createService(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/{service_id}/employee")
    public ResponseEntity<ResponseDataAPI> getListEmployeeOfService(@PathVariable("service_id") String serviceId, @RequestParam(value = "type_sort", required = false) String typeSort,
                                                                    @RequestParam(value = "column_sort", required = false) String columnSort) {

        return ResponseEntity.ok(ResponseDataAPI.builder().data(employeeServiceService.getListUserOfServiceId(serviceId, columnSort, typeSort)).build());
    }


    @GetMapping("/{service_id}/discount")
    public ResponseEntity<ResponseDataAPI> getListDiscountOfService(@PathVariable("service_id") String serviceId, @RequestParam(value = "type_sort", required = false) String typeSort,
                                                                    @RequestParam(value = "column_sort", required = false) String columnSort) {

        return ResponseEntity.ok(ResponseDataAPI.builder().data(employeeServiceService.getListDiscountOfService(serviceId, columnSort, typeSort)).build());
    }

    @GetMapping("/overview")
    private ResponseEntity<Object> getOveriewOfTask(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(service.getServiceOverviewDetail(startDate, endDate)).build());
    }

    @GetMapping("/overview/detail")
    private ResponseEntity<Object> getOveriewDetailOfTask(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(service.getOverviewDetailOfService(startDate, endDate)).build());
    }


    @GetMapping("/overview/employee")
    private ResponseEntity<Object> getOveriewOfTaskForEmployee(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(service.getServiceOverviewDetailForEmployee(startDate, endDate)).build());
    }

    @GetMapping("/overview/employee/detail")
    private ResponseEntity<Object> getOveriewDetailOfTaskForEmployee(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(service.getOverviewDetailOfServiceForEmployee(startDate, endDate)).build());
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatusService(@RequestBody ChangeStatusDomain domain) {
        service.changeStatusService(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

}
