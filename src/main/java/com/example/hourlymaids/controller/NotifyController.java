package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.service.NotifyService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notify")
public class NotifyController {
    @Autowired
    private NotifyService notifyService;

    @PostMapping("")
    public ResponseEntity<Object> createNotify(@RequestBody NotifyDomain domain) {
        notifyService.createNotify(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("")
    public ResponseEntity<Object> getListNotify(@RequestParam(value = "offset") @ApiParam(value = "offset", example = "0") Integer offset,
                                                @RequestParam(value = "limit") @ApiParam(value = "limit", example = "10") Integer limit,
                                                @RequestParam(value = "status") @ApiParam(value = "status", example = "1") String status,
                                                @RequestParam(value = "value_search") @ApiParam(value = "valueSearch", example = "") String valueSearch,
                                                @RequestParam(value = "column_sort") @ApiParam(value = "columnSort", example = "TITLE") String columnSort,
                                                @RequestParam(value = "type_sort") @ApiParam(value = "typeSort", example = "ASC") String typeSort) {
        Integer of = (offset == null || offset <= 1) ? 0 : offset - 1;
        Integer lim = (limit == null || limit < 1) ? 10 : limit;
        GetListRequest getListRequest = new GetListRequest();
        getListRequest.setLimit(lim);
        getListRequest.setOffset(of);
        getListRequest.setColumnSort(columnSort);
        getListRequest.setTypeSort(typeSort);
        getListRequest.setValueSearch(valueSearch);
        getListRequest.setStatus(status);
        ResponseDataAPI responseDataAPI = notifyService.getListNotify(getListRequest);
        return ResponseEntity.ok(responseDataAPI);

    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeNotifyStatus(@RequestBody ChangeNotifyStatusDomain domain) {
        notifyService.changeStatusNotify(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/{notify_id}")
    public ResponseEntity<Object> getDetailNotify(@PathVariable("notify_id") String notifyId) {
        NotifyDomain domain = notifyService.getDetailNotify(notifyId);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(domain).build());
    }

    @PutMapping("/{notify_id}")
    public ResponseEntity<ResponseDataAPI> editMessageDetail(@PathVariable("notify_id") String notifyId, @RequestBody NotifyDomain notifyDomain) {
        notifyDomain.setId(notifyId);
        notifyService.editNotify(notifyDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/delete")
    public ResponseEntity<ResponseDataAPI> deleteNotify(@RequestBody DeleteDomain deleteDomain) {
        notifyService.deleteNotify(deleteDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

}
