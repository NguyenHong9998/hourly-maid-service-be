package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.constant.*;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.entity.NotifyEntity;
import com.example.hourlymaids.entity.ServiceCompanyEntity;
import com.example.hourlymaids.repository.NotifyRepository;
import com.example.hourlymaids.util.DateTimeUtils;
import com.example.hourlymaids.util.StringUtils;
import com.example.hourlymaids.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.jms.Topic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotifyServiceImpl implements NotifyService {
    @Autowired
    private NotifyRepository notifyRepository;

    @Override
    public void createNotify(NotifyDomain domain) {
        String title = domain.getTitle();
        if (StringUtils.isEmpty(title)) {
            throw new CustomException(Error.TITLE_EMPTY.getMessage(), Error.TITLE_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }

        String content = domain.getContent();
        if (StringUtils.isEmpty(content)) {
            throw new CustomException(Error.CONTENT_EMPTY.getMessage(), Error.CONTENT_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }
        String topic = domain.getType();
        NotifyTopic notifyTopic = NotifyTopic.getToppicByValue(topic);
        NotifyEntity notifyEntity = new NotifyEntity();
        notifyEntity.setContent(content);
        notifyEntity.setTitle(title);
        notifyEntity.setTopic(notifyTopic.getCode());
        notifyEntity.setStatus(NotifyStatus.UN_PUBLISH.getCode());

        notifyRepository.save(notifyEntity);
    }

    @Override
    public ResponseDataAPI getListNotify(GetListRequest request) {
        List<String> columnSort = Arrays.asList(ColumnSortNotify.TITLE.getName(), ColumnSortNotify.STATUS.getName(), ColumnSortNotify.CONTENT.getName(), ColumnSortNotify.TYPE.getName());
        Pageable pageable = null;

        if (columnSort.contains(request.getColumnSort())) {
            if (ColumnSortNotify.TITLE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortNotify.TITLE.getValue());
            } else if (ColumnSortNotify.STATUS.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortNotify.STATUS.getValue());
            } else if (ColumnSortNotify.TYPE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortNotify.TYPE.getValue());
            } else if (ColumnSortNotify.CONTENT.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortNotify.CONTENT.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortNotify.CREATE_DATE.getValue()).ascending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch()).toUpperCase();

        Page<NotifyEntity> entities;
        String status = request.getStatus();
        if (StringUtils.isEmpty(status)) {
            entities = notifyRepository.findAllNotify(valueSearch, pageable);
        } else {
            NotifyStatus notifyStatus = NotifyStatus.getStatusByValue(status);
            if (notifyStatus.equals(NotifyStatus.ALL)) {
                entities = notifyRepository.findAllNotify(valueSearch, pageable);
            } else {
                entities = notifyRepository.findAllNotifyAndStatus(notifyStatus.getCode(), valueSearch, pageable);
            }
        }


        List<Object> result = entities.stream().map(notify -> {
            NotifyDomain notifyDomain = new NotifyDomain();
            notifyDomain.setTitle(notify.getTitle());
            notifyDomain.setContent(notify.getContent());
            notifyDomain.setStatus(NotifyStatus.getStatusByCode(notify.getStatus()).getValue());
            notifyDomain.setType(NotifyTopic.getTopicByCode(notify.getTopic()).getValue());
            notifyDomain.setId(StringUtils.convertObjectToString(notify.getId()));
            return notifyDomain;
        }).collect(Collectors.toList());

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(result);
        responseDataAPI.setTotalRows(entities.getTotalElements());

        return responseDataAPI;
    }

    private Pageable getPageable(GetListRequest request, Pageable pageable) {
        if (ConstantDefine.SORT_ASC.equals(request.getTypeSort())) {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.asc(request.getColumnSort())));
        } else if (ConstantDefine.SORT_DESC.equals(request.getTypeSort())) {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.desc(request.getColumnSort())));
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.desc(ColumnSortNotify.CREATE_DATE.getValue())));
        }
        return pageable;
    }

    @Override
    public ResponseDataAPI getListNotifyForUser(GetListRequest request) {
        Pageable pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortNotify.PUBLISH_DATE.getValue()).descending());
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch()).toUpperCase();
        List<Integer> topics = new ArrayList<>();
        topics.add(NotifyTopic.ALL.getCode());
        String roleName = UserUtils.getCurrentUser().getRoles().get(0).getName();
        if(UserUtils.getCurrentUser().getRoles().get(0).getName().equals(UserRole.USER.getName())){
            topics.add(NotifyTopic.TO_USER.getCode());
        }else {
            topics.add(NotifyTopic.TO_EMPLOYEE.getCode());
        }
        Page<NotifyEntity> entities = notifyRepository.findAllNotifyForUser(topics, valueSearch, pageable);

        List<Object> result = entities.stream().map(notify -> {
            NotifyDomain notifyDomain = new NotifyDomain();
            notifyDomain.setTitle(notify.getTitle());
            notifyDomain.setContent(notify.getContent());
            notifyDomain.setStatus(NotifyStatus.getStatusByCode(notify.getStatus()).getValue());
            notifyDomain.setType(NotifyTopic.getTopicByCode(notify.getTopic()).getValue());
            notifyDomain.setId(StringUtils.convertObjectToString(notify.getId()));
            notifyDomain.setPublishDate(StringUtils.convertDateToStringFormatPattern(notify.getPublishDate(), DateTimeUtils.YYYYMMDDhhmmss));
            return notifyDomain;
        }).collect(Collectors.toList());

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(result);
        responseDataAPI.setTotalRows(entities.getTotalElements());

        return responseDataAPI;
    }


    @Override
    public void changeStatusNotify(ChangeNotifyStatusDomain domain) {
        Long id = StringUtils.convertObjectToLongOrNull(domain.getId());
        if (id == null) {
            throw new CustomException(Error.NOTIFY_NOT_FOUND.getMessage(), Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        NotifyEntity notifyEntity = notifyRepository.findById(id).orElse(null);
        if (notifyEntity == null) {
            throw new CustomException(Error.NOTIFY_NOT_FOUND.getMessage(), Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        notifyEntity.setStatus(NotifyStatus.PUBLISH.getCode());
        notifyEntity.setPublishDate(new Date());
        notifyRepository.save(notifyEntity);
    }

    @Override
    public NotifyDomain getDetailNotify(String id) {
        Long notifyId = StringUtils.convertObjectToLongOrNull(id);
        if (notifyId == null) {
            throw new CustomException(Error.NOTIFY_NOT_FOUND.getMessage(), Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        NotifyEntity notifyEntity = notifyRepository.findById(notifyId).orElse(null);
        if (notifyEntity == null) {
            throw new CustomException(Error.NOTIFY_NOT_FOUND.getMessage(), Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }

        NotifyDomain notifyDomain = new NotifyDomain();
        notifyDomain.setId(notifyEntity.getId().toString());
        notifyDomain.setStatus(NotifyStatus.getStatusByCode(notifyEntity.getStatus()).getValue());
        notifyDomain.setType(NotifyTopic.getTopicByCode(notifyEntity.getTopic()).getValue());
        notifyDomain.setPublishDate(StringUtils.convertDateToStringFormatPattern(notifyEntity.getPublishDate(), DateTimeUtils.YYYYMMDDhhmmss));
        notifyDomain.setTitle(notifyEntity.getTitle());
        notifyDomain.setContent(notifyEntity.getContent());
        return notifyDomain;
    }

    @Override
    public void editNotify(NotifyDomain domain) {
        Long notifyId = StringUtils.convertObjectToLongOrNull(domain.getId());
        if (notifyId == null) {
            throw new CustomException(Error.NOTIFY_NOT_FOUND.getMessage(), Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        NotifyEntity notifyEntity = notifyRepository.findById(notifyId).orElse(null);
        if (notifyEntity == null) {
            throw new CustomException(Error.NOTIFY_NOT_FOUND.getMessage(), Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }

        notifyEntity.setTitle(domain.getTitle());
        notifyEntity.setContent(domain.getContent());
        notifyEntity.setTopic(NotifyTopic.getToppicByValue(domain.getType()).getCode());
        notifyRepository.save(notifyEntity);
    }

    @Override
    public void deleteNotify(DeleteDomain deleteDomain) {
        List<String> ids = deleteDomain.getIds();
        if (!CollectionUtils.isEmpty(ids)) {
            List<Long> notifyIds = ids.stream().map(id -> StringUtils.convertObjectToLongOrNull(id)).filter(t -> t != null).collect(Collectors.toList());
            List<NotifyEntity> entities = notifyRepository.findAllByIds(notifyIds);

            List<NotifyEntity> deleteEntities = entities.stream().map(t -> {
                t.setIsDeleted(1);
                t.setUpdatedBy(UserUtils.getCurrentUserId());
                t.setUpdatedDate(new Date());
                return t;
            }).collect(Collectors.toList());

            notifyRepository.saveAll(deleteEntities);

        }
    }
}
