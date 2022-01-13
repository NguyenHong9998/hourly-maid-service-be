package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.DiscountDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/discount")
public class DiscountController {
    @Autowired
    private DiscountService service;

    @GetMapping("")
    public ResponseEntity<ResponseDataAPI> getListDiscount(@RequestParam(value = "offset", required = false) Integer offset,
                                                          @RequestParam(value = "limit", required = false) Integer limit,
                                                          @RequestParam(value = "value_search", required = false) String valueSearch,
                                                          @RequestParam(value = "type_sort", required = false) String typeSort,
                                                          @RequestParam(value = "column_sort", required = false) String columnSort) {
        GetListRequest request = new GetListRequest(limit, offset, valueSearch, columnSort, typeSort);
        ResponseDataAPI dataAPI = service.getListDiscount(request);
        return ResponseEntity.ok(dataAPI);
    }

    @GetMapping("/{discount_id}")
    public ResponseEntity<ResponseDataAPI> getDiscountDetail(@PathVariable("discount_id") String discountId) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(service.getDiscountDetail(discountId)).build());
    }

    @PutMapping("/{discount_id}")
    public ResponseEntity<ResponseDataAPI> editDiscountDetail(@PathVariable("discount_id") String discountId, @RequestBody DiscountDomain domain) {
        service.updateDiscount(discountId, domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseDataAPI> createDiscount(@RequestBody DiscountDomain domain) {
        service.createDiscount(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

}
