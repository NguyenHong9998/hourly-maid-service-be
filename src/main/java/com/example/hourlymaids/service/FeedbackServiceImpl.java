package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.constant.FeedbackType;
import com.example.hourlymaids.domain.FeedbackDetailDomain;
import com.example.hourlymaids.domain.FeedbackDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.OverviewFeedbackDomain;
import com.example.hourlymaids.entity.ClientEntity;
import com.example.hourlymaids.entity.FeedbackEntity;
import com.example.hourlymaids.entity.UserEntity;
import com.example.hourlymaids.repository.ClientRepository;
import com.example.hourlymaids.repository.FeedbackRepository;
import com.example.hourlymaids.repository.UserRepository;
import com.example.hourlymaids.util.StringUtils;
import com.example.hourlymaids.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ResponseDataAPI getListFeedback(String userId) {
        Long user = StringUtils.convertStringToLongOrNull(userId);

        List<Object[]> entities = feedbackRepository.findAllFeedbackByEmployeeId(user);

        List<Object> result = entities.stream().map(objects -> {
            FeedbackEntity feedbackEntity = (FeedbackEntity) objects[0];
            FeedbackDomain feedbackDomain = new FeedbackDomain();
            feedbackDomain.setContent(feedbackEntity.getContent());
            feedbackDomain.setUserId(feedbackEntity.getUserId().toString());
            feedbackDomain.setType(feedbackEntity.getType().toString());
            feedbackDomain.setEmployeeId(feedbackEntity.getEmployeeId().toString());
            Long clientId = StringUtils.convertObjectToLongOrNull(objects[1]);
            ClientEntity userEntity = clientRepository.findById(clientId).orElse(null);

            feedbackDomain.setUsername(userEntity.getFullName());
            feedbackDomain.setAvatarUser(userEntity.getAvatar());
            return feedbackDomain;
        }).collect(Collectors.toList());

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(result);
        responseDataAPI.setTotalRows(entities.size());
        return responseDataAPI;
    }

    @Override
    public void createFeedback(FeedbackDomain feedbackDomain) {
        FeedbackEntity feedbackEntity = new FeedbackEntity();
        Long employeeId = StringUtils.convertStringToLongOrNull(feedbackDomain.getEmployeeId());
        if (employeeId == null) {
            if (FeedbackType.FEEDBACK_EMPLOYEE.getCode().equals(StringUtils.convertStringToIntegerOrNull(feedbackDomain.getType()))) {
                throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }
        } else {
            UserEntity employee = userRepository.getById(employeeId);
            if (employee == null) {
                throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }
            feedbackEntity.setEmployeeId(employeeId);
        }

        if (StringUtils.isEmpty(feedbackDomain.getContent())) {
            throw new CustomException(Error.CoNTENT_FEEDBACk_NULL.getMessage(), Error.CoNTENT_FEEDBACk_NULL.getCode(), HttpStatus.BAD_REQUEST);
        }
        feedbackEntity.setType(StringUtils.convertStringToIntegerOrNull(feedbackDomain.getType()));
        feedbackEntity.setUserId(UserUtils.getCurrentUserId());
        feedbackEntity.setContent(feedbackDomain.getContent());
        feedbackEntity.setRateNumber(StringUtils.convertStringToIntegerOrNull(feedbackDomain.getVoteNum()));
        feedbackRepository.save(feedbackEntity);
    }

    @Override
    public void updateFeedback(String feedbackId, FeedbackDomain feedbackDomain) {
        Long userId = UserUtils.getCurrentUserId();
        Long feedback = StringUtils.convertStringToLongOrNull(feedbackId);
        if (feedback == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        FeedbackEntity feedbackEntity = feedbackRepository.getById(feedback);
        if (feedbackEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (feedbackEntity.getUserId() != userId) {
            throw new CustomException(Error.CANNOT_UPDATE_ANOTHER_FEEDBACL.getMessage(), Error.CANNOT_UPDATE_ANOTHER_FEEDBACL.getCode(), HttpStatus.BAD_REQUEST);
        }

        feedbackEntity.setContent(feedbackDomain.getContent());
        feedbackEntity.setUpdatedBy(userId);
        feedbackEntity.setUpdatedDate(new Date());
        feedbackEntity.setRateNumber(StringUtils.convertStringToIntegerOrNull(feedbackDomain.getVoteNum()));

        feedbackRepository.save(feedbackEntity);
    }

    @Override
    public OverviewFeedbackDomain getOverviewFeedbackOfUser(String userId) {
        Long id = StringUtils.convertObjectToLongOrNull(userId);
        Integer numClient = feedbackRepository.findAllFeedbackDistinctByEmployeeId(id).size();
        Integer numberFeedbackOfUser = feedbackRepository.findAllFeedbackByEmployeeId(id).size();
        List<FeedbackDetailDomain> detail = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            FeedbackDetailDomain domain = new FeedbackDetailDomain();
            domain.setNumStar(String.valueOf(i));
            Integer numFeedbackByRate = feedbackRepository.findFeedbackEntityByRateNumberAndEmployeeId(i, id).size();
            domain.setNumUser(numFeedbackByRate.toString());
            domain.setPercent(new DecimalFormat("0.00").format(numberFeedbackOfUser == 0 ? 0 : numFeedbackByRate / numberFeedbackOfUser * 100));
            detail.add(domain);
        }
        OverviewFeedbackDomain result = new OverviewFeedbackDomain();
        result.setNumUser(numClient.toString());
        result.setDetail(detail);
        return result;
    }
}
