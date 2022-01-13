package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.ServiceDomain;
import com.example.hourlymaids.service.ServiceCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/service")
public class ServiceCompanyController {

    @Autowired
    private ServiceCompanyService service;

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

}
