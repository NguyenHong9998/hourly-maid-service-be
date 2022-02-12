package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.FeedbackDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.OverviewFeedbackDomain;

public interface FeedbackService {
    ResponseDataAPI getListFeedback(String userId);

    void createFeedback(FeedbackDomain feedbackDomain);

    void updateFeedback(String feedbackId, FeedbackDomain feedbackDomain);

    OverviewFeedbackDomain getOverviewFeedbackOfUser(String userId);


}
