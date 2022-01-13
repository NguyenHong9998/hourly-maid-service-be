package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.FeedbackDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService service;

    @GetMapping("")
    public ResponseEntity<ResponseDataAPI> getListDiscount(@RequestParam(value = "offset", required = false) Integer offset,
                                                           @RequestParam(value = "limit", required = false) Integer limit,
                                                           @RequestParam(value = "type") Integer type,
                                                           @RequestParam(value = "user_id", required = false) String userId
    ) {
        GetListRequest request = new GetListRequest();
        request.setLimit(limit);
        request.setOffset(offset);
        ResponseDataAPI dataAPI = service.getListFeedback(userId, request, type);
        return ResponseEntity.ok(dataAPI);
    }

    @PutMapping("/{feedback_id}")
    public ResponseEntity<ResponseDataAPI> editFeedbackDetail(@PathVariable("feedback_id") String feedbackId, @RequestBody FeedbackDomain domain) {
        service.updateFeedback(feedbackId, domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseDataAPI> createDiscount(@RequestBody FeedbackDomain domain) {
        service.createFeedback(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }


}
