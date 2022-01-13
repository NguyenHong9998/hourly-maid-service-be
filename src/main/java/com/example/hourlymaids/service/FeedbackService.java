package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.FeedbackDomain;
import com.example.hourlymaids.domain.GetListRequest;

public interface FeedbackService {
    ResponseDataAPI getListFeedback(String userId, GetListRequest request, Integer type);

    void createFeedback(FeedbackDomain feedbackDomain);

    void updateFeedback(String feedbackId, FeedbackDomain feedbackDomain);
}
