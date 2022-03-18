package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.constant.*;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.entity.*;
import com.example.hourlymaids.repository.LeaveDateRepository;
import com.example.hourlymaids.repository.RoleRepository;
import com.example.hourlymaids.repository.TaskRepository;
import com.example.hourlymaids.repository.UserRepository;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveDateServiceImpl implements LeaveDateService {

    @Autowired
    private LeaveDateRepository leaveDateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void createLeaveDate(LeaveDateDomain leaveDateDomain) {
        Long userId = StringUtils.convertObjectToLongOrNull(leaveDateDomain.getUserId());
        String note = leaveDateDomain.getNote();
        if (userId == null) {
            throw new CustomException("Không tìm thấy nhân viên phù hợp hãy thử lại", Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(note)) {
            throw new CustomException(Error.EMPTY_NOTE.getMessage(), Error.EMPTY_NOTE.getCode(), HttpStatus.BAD_REQUEST);
        }
        List<TaskEntity> taskEntities = taskRepository.findByEmployeeId(userId);
        List<LeaveDomain> leave = leaveDateDomain.getLeaveDomains();
        if (!CollectionUtils.isEmpty(leave)) {
            List<LeaveDateEntity> entities = leave.stream().map(t -> {
                LeaveDateEntity entity = new LeaveDateEntity();
                entity.setLeaveDate(DateTimeUtils.convertStringToDateOrNull(t.getDate(), DateTimeUtils.DDMMYYYY));
                entity.setNote(note);
                entity.setUserId(userId);
                Date start = DateTimeUtils.convertStringToDateOrNull(t.getStart(), DateTimeUtils.DDMMYYYYHHMMSS);
                Date end = DateTimeUtils.convertStringToDateOrNull(t.getEnd(), DateTimeUtils.DDMMYYYYHHMMSS);
                if (start.after(end)) {
                    throw new CustomException("Thời gian nghỉ không hợp lệ", "S00000", HttpStatus.BAD_REQUEST);
                }
                List<TaskEntity> taskOnLeaveDate = taskEntities.stream().filter(task ->
                        (task.getCancelTime() == null || task.getPaidTime() == null)).filter(
                        task ->
                                (task.getCompleteTime().getTime() > start.getTime() && task.getCompleteTime().getTime() < end.getTime())

                                        || (task.getStartTime().getTime() > start.getTime() && task.getStartTime().getTime() < end.getTime())
                ).collect(Collectors.toList());

                if (taskOnLeaveDate.size() != 0) {
                    throw new CustomException("Tồn tại công việc có thời gian làm trùng với thời gian nghỉ trong ngày " + t.getDate(), "S000000", HttpStatus.BAD_REQUEST);
                }
                entity.setStart(start);
                entity.setEnd(end);
                return entity;
            }).collect(Collectors.toList());

            leaveDateRepository.saveAll(entities);
        }
    }

    @Override
    public void editLeaveDate(LeaveDateDomain leaveDateDomain) {
        Long userId = StringUtils.convertObjectToLongOrNull(leaveDateDomain.getUserId());
        String note = leaveDateDomain.getNote();
        if (userId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(note)) {
            throw new CustomException(Error.EMPTY_NOTE.getMessage(), Error.EMPTY_NOTE.getCode(), HttpStatus.BAD_REQUEST);
        }
        LeaveDateEntity entity = leaveDateRepository.findById(StringUtils.convertObjectToLongOrNull(leaveDateDomain.getId())).orElse(null);
        List<TaskEntity> taskEntities = taskRepository.findByEmployeeId(userId);

        List<LeaveDomain> leave = leaveDateDomain.getLeaveDomains();
        if (!CollectionUtils.isEmpty(leave)) {
            List<LeaveDateEntity> entities = leave.stream().map(t -> {
                entity.setLeaveDate(DateTimeUtils.convertStringToDateOrNull(t.getDate(), DateTimeUtils.DDMMYYYY));
                entity.setNote(note);
                entity.setUserId(userId);
                Date start = DateTimeUtils.convertStringToDateOrNull(t.getStart(), DateTimeUtils.DDMMYYYYHHMMSS);
                Date end = DateTimeUtils.convertStringToDateOrNull(t.getEnd(), DateTimeUtils.DDMMYYYYHHMMSS);
                if (start.after(end)) {
                    throw new CustomException("Thời gian nghỉ không hợp lệ", "S00000", HttpStatus.BAD_REQUEST);
                }

                List<TaskEntity> taskOnLeaveDate = taskEntities.stream().filter(task ->
                        (task.getCancelTime() == null || task.getPaidTime() == null)).filter(
                        task ->
                                (task.getCompleteTime().getTime() > start.getTime() && task.getCompleteTime().getTime() < end.getTime())

                                        || (task.getStartTime().getTime() > start.getTime() && task.getStartTime().getTime() < end.getTime())
                ).collect(Collectors.toList());

                if (taskOnLeaveDate.size() != 0) {
                    throw new CustomException("Tồn tại công việc có thời gian làm trùng với thời gian nghỉ trong ngày " + t.getDate(), "S000000", HttpStatus.BAD_REQUEST);
                }
                entity.setStart(start);
                entity.setEnd(end);
                return entity;
            }).collect(Collectors.toList());

            leaveDateRepository.saveAll(entities);
        }
    }

    @Override
    public ResponseDataAPI getListLeaveDate(GetListRequest request, String leaveDate) {
        List<String> columnSort = Arrays.asList(ColumnSortLeaveDate.NAME.getName(), ColumnSortLeaveDate.START.getName(), ColumnSortLeaveDate.LEAVE_DATE.getName(), ColumnSortLeaveDate.END.getName());
        Pageable pageable = null;
        if (columnSort.contains(request.getColumnSort())) {
            if (ColumnSortLeaveDate.NAME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortLeaveDate.NAME.getValue());
            } else if (ColumnSortLeaveDate.START.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortLeaveDate.START.getValue());
            } else if (ColumnSortLeaveDate.END.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortLeaveDate.END.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortLeaveDate.CREATED_DATE.getValue()).descending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch()).toUpperCase();
        Date tmpDate = DateTimeUtils.convertStringToDateOrNull(leaveDate, DateTimeUtils.YYYYMMDD);
        Page<Object[]> entities = leaveDateRepository.findByLeaveDate(tmpDate, valueSearch, pageable);

        List<Object> result = entities.stream().map(objects -> {
            GetListLeaveDateDomain domain = new GetListLeaveDateDomain();
            LeaveDateEntity leaveDateEntity = (LeaveDateEntity) objects[0];
            domain.setAvatar(StringUtils.convertObjectToString(objects[2]));
            domain.setName(StringUtils.convertObjectToString(objects[1]));
            domain.setId(leaveDateEntity.getId().toString());
            domain.setStart(StringUtils.convertDateToStringFormatPattern(leaveDateEntity.getStart(), DateTimeUtils.hhmmss));
            domain.setEnd(StringUtils.convertDateToStringFormatPattern(leaveDateEntity.getEnd(), DateTimeUtils.hhmmss));
            return domain;
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
    public List<String> getListDateHasLeaveDate() {
        List<Date> dateList = leaveDateRepository.getListLeaveDate();
        return dateList.stream().map(date -> StringUtils.convertDateToStringFormatyyyyMMdd(date)).collect(Collectors.toList());
    }

    @Override
    public LeaveDateDomain getLeaveDateInform(String leaveId) {
        Long id = StringUtils.convertObjectToLongOrNull(leaveId);
//        List<Object> inform = leaveDateRepository.getLeaveDateInform(id);
        LeaveDateDomain domain = new LeaveDateDomain();
        LeaveDateEntity leaveDateEntity = leaveDateRepository.findById(id).orElse(null);
        UserEntity userEntity = userRepository.findById(leaveDateEntity.getUserId()).orElse(null);
        String username = StringUtils.convertObjectToString(userEntity.getFullName());
        String avatar = StringUtils.convertObjectToString(userEntity.getAvatar());
        String userId = StringUtils.convertObjectToString(userEntity.getId());
        String leaveDate = StringUtils.convertDateToStringFormatyyyyMMdd(leaveDateEntity.getLeaveDate());
        String note = leaveDateEntity.getNote();
        String start = StringUtils.convertDateToStringFormatPattern(leaveDateEntity.getStart(), "HH:mm");
        String end = StringUtils.convertDateToStringFormatPattern(leaveDateEntity.getEnd(), "HH:mm");

        domain.setId(leaveId);
        domain.setUserId(userId);
        domain.setUsername(username);
        domain.setNote(note);
        domain.setAvatar(avatar);
        LeaveDomain leaveDomain = new LeaveDomain();
        leaveDomain.setDate(leaveDate);
        leaveDomain.setStart(start);
        leaveDomain.setEnd(end);
        domain.setLeaveDomains(Arrays.asList(leaveDomain));
        return domain;
    }

    @Override
    public void deleteLeaveDate(DeleteDomain domain) {
        List<String> idString = domain.getIds();
        List<Long> ids = idString.stream().map(t -> StringUtils.convertObjectToLongOrNull(t)).filter(t -> t != null).collect(Collectors.toList());
        for (Long id : ids) {
            LeaveDateEntity leaveDateEntity = leaveDateRepository.findById(id).orElse(null);

            String roleName = UserUtils.getCurrentUser().getRoles().get(0).getName();

            if (!UserRole.MANAGER.getName().equals(roleName) && UserUtils.getCurrentUserId() != leaveDateEntity.getUserId()) {
                throw new CustomException("Không thể xoá ngày nghỉ của nhân viên khác, kiểm tra và thử lại", "S0000", HttpStatus.BAD_REQUEST);
            }
            leaveDateEntity.setIsDeleted(1);
            leaveDateEntity.setUpdatedDate(new Date());
            leaveDateEntity.setUpdatedBy(UserUtils.getCurrentUserId());
            leaveDateRepository.save(leaveDateEntity);

        }
    }
}
