package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.DeleteDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.LeaveDateDomain;
import com.example.hourlymaids.domain.NotifyDomain;
import com.example.hourlymaids.service.LeaveDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leave-date")
public class LeaveDateController {
    @Autowired
    private LeaveDateService leaveDateService;

    @PostMapping("")
    public ResponseEntity<Object> createLeaveDate(@RequestBody LeaveDateDomain domain) {
        leaveDateService.createLeaveDate(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PutMapping("/{leave_id}")
    public ResponseEntity<Object> eidtLeaveDate(@PathVariable("leave_id") String leaveId, @RequestBody LeaveDateDomain domain) {
        domain.setId(leaveId);
        leaveDateService.editLeaveDate(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/{leave_id}")
    public ResponseEntity<Object> getLeaveDateInform(@PathVariable("leave_id") String leaveId) {

        return ResponseEntity.ok(ResponseDataAPI.builder().data(leaveDateService.getLeaveDateInform(leaveId)).build());

    }

    @GetMapping("")
    public ResponseEntity<Object> getListLeaveDateByDate(@RequestParam(value = "offset", required = false) Integer offset,
                                                         @RequestParam(value = "limit", required = false) Integer limit,
                                                         @RequestParam(value = "value_search", required = false) String valueSearch,
                                                         @RequestParam(value = "type_sort", required = false) String typeSort,
                                                         @RequestParam(value = "column_sort", required = false) String columnSort,
                                                         @RequestParam(value = "date", required = false) String date) {
        Integer of = (offset == null || offset <= 1) ? 0 : offset - 1;
        Integer lim = (limit == null || limit < 1) ? 10 : limit;
        GetListRequest getListRequest = new GetListRequest();
        getListRequest.setLimit(lim);
        getListRequest.setOffset(of);
        getListRequest.setColumnSort(columnSort);
        getListRequest.setTypeSort(typeSort);
        getListRequest.setValueSearch(valueSearch);
        ResponseDataAPI responseDataAPI = leaveDateService.getListLeaveDate(getListRequest, date);
        return ResponseEntity.ok(responseDataAPI);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getListDateHasLeaveDate() {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(leaveDateService.getListDateHasLeaveDate()).build());
    }

    @PostMapping("/delete")
    public ResponseEntity<Object> deleteLeaveDate(@RequestBody DeleteDomain deleteDomain) {
        leaveDateService.deleteLeaveDate(deleteDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }
}
