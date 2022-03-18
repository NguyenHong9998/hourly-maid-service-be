package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.constant.*;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.entity.*;
import com.example.hourlymaids.repository.*;
import com.example.hourlymaids.util.DateTimeUtils;
import com.example.hourlymaids.util.SendMailUtils;
import com.example.hourlymaids.util.StringUtils;
import com.example.hourlymaids.util.UserUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private ServiceCompanyRepository serviceCompanyRepository;

    @Autowired
    private ServiceDiscountRepository serviceDiscountRepository;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private LeaveDateRepository leaveDateRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SendMailUtils sendMailUtils;

    @Autowired
    private UserRepository userRepository;

    private String link = "http://hourly-maid-service-client.herokuapp.com/";

    @Autowired
    private EmployeeServiceRepository employeeServiceRepository;

    @Override
    public CheckPriceResponseDomain getPriceList(CheckPriceDomain domain) {
        List<Long> serviceId = domain.getServiceId().stream().map(t -> StringUtils.convertObjectToLongOrNull(t)).filter(t -> t != null).collect(Collectors.toList());
        String startTime = domain.getStartTime();
        String endTime = domain.getEndTime();

        Date start = DateTimeUtils.convertStringToDateOrNull(startTime, DateTimeUtils.YYYYMMDDhhmmss);
        Date end = DateTimeUtils.convertStringToDateOrNull(endTime, DateTimeUtils.YYYYMMDDhhmmss);

        Period p = new Period(new DateTime(start.getTime()), new DateTime(end.getTime()));
        int hours = p.getHours();
        int minutes = p.getMinutes();
        double totalHours = hours + (double) minutes / 60;

        List<ServiceCompanyEntity> serviceCompanyEntities = serviceCompanyRepository.findAllById(serviceId);
        List<PriceListDomain> result = new ArrayList<>();

        for (ServiceCompanyEntity service : serviceCompanyEntities) {
            List<ServiceDiscountEntity> discountEntities = serviceDiscountRepository.findByServiceId(service.getId()).stream().sorted(Comparator.comparingLong(ServiceDiscountEntity::getSalePercentage)).collect(Collectors.toList());

            ServiceDiscountEntity mostDiscount = discountEntities.size() > 0 ? discountEntities.get(0) : null;
            PriceListDomain item = new PriceListDomain();

            item.setHours(new DecimalFormat("0").format(totalHours));
            item.setServiceName(service.getServiceName());
            item.setServicePrice(new DecimalFormat("0").format(service.getPrice()));
            if (mostDiscount != null) {
                DiscountEntity discountEntity = discountRepository.findById(mostDiscount.getDiscountId()).orElse(null);
                item.setDiscountApply(discountEntity == null ? "" : discountEntity.getTitle());
                item.setPercentApply(mostDiscount.getSalePercentage().toString());
                double price = service.getPrice() * totalHours * (100 - mostDiscount.getSalePercentage()) / 100;
                item.setPrice(new DecimalFormat("0").format(price));
            } else {
                double price = service.getPrice() * totalHours;
                item.setPrice(new DecimalFormat("0").format(price));
            }
            result.add(item);
        }
        CheckPriceResponseDomain response = new CheckPriceResponseDomain();
        response.setPriceList(result);
        double total = result.stream().map(t -> Double.valueOf(t.getPrice())).reduce((t1, t2) -> Double.sum(t1, t2)).get();
        response.setTotal(new DecimalFormat("0.00").format(total));
        return response;
    }

    @Override
    public void createTaskDomain(CreateTaskDomain domain) {
        String email = domain.getEmail();
        String phone = domain.getPhone();
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(phone)) {
            throw new CustomException(Error.EMAIL_OR_PHONE_EMPTY.getMessage(), Error.EMAIL_OR_PHONE_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity;
        userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setEmail(email);
//            String password = RandomStringUtils.randomAlphanumeric(6);
            userEntity.setPassword(new BCryptPasswordEncoder().encode("abc1234"));
            userEntity.setAvatar("https://www.sibberhuuske.nl/wp-content/uploads/2016/10/default-avatar.png");
            userEntity.setFullName(domain.getUserName());
            userEntity.setPhoneNumber(domain.getPhone());
            userEntity.setRoleId(4l);
            userEntity = userRepository.save(userEntity);

            sendMailToClient(email, "abc1234", userEntity.getFullName());
        }

        TaskEntity taskEntity = new TaskEntity();
        List<Long> serviceId = domain.getServiceId().stream().map(t -> StringUtils.convertObjectToLongOrNull(t)).filter(t -> t != null).collect(Collectors.toList());
        String startTime = domain.getStartTime();
        String endTime = domain.getEndTime();

        Date start = DateTimeUtils.convertStringToDateOrNull(startTime, DateTimeUtils.YYYYMMDDhhmmss);
        Date end = DateTimeUtils.convertStringToDateOrNull(endTime, DateTimeUtils.YYYYMMDDhhmmss);
        Date milestoneDate = DateTimeUtils.convertStringToDateOrNull(startTime, DateTimeUtils.YYYYMMDD);

        taskEntity.setWorkDate(milestoneDate);
        taskEntity.setUserId(userEntity.getId());
//        taskEntity.setNumberUser(StringUtils.convertStringToIntegerOrNull(domain.getNumOfEmployee()));
        taskEntity.setStartTime(start);
        taskEntity.setCompleteTime(end);
        taskEntity.setAddress(domain.getAddress());
        taskEntity.setNote(domain.getNote());
        List<ServiceCompanyEntity> serviceCompanyEntities = serviceCompanyRepository.findAllById(serviceId);

        for (ServiceCompanyEntity service : serviceCompanyEntities) {
            List<ServiceDiscountEntity> discountEntities = serviceDiscountRepository.findByServiceId(service.getId()).stream().sorted(Comparator.comparingLong(ServiceDiscountEntity::getSalePercentage)).collect(Collectors.toList());
            ServiceDiscountEntity mostDiscount = discountEntities.size() > 0 ? discountEntities.get(0) : null;
            if (mostDiscount != null) {
                taskEntity.setDiscountServiceId(mostDiscount.getId());
            }
        }
        taskEntity.setServiceId(serviceId.get(0));
        taskEntity = taskRepository.save(taskEntity);
    }


    void sendMailToClient(String email, String password, String name) {
        SendMailDomain sendMailDomain = new SendMailDomain();
        sendMailDomain.setToEmail(Arrays.asList(email));
        sendMailDomain.setMessageContent("");
        String subject = "CleanMe gửi bạn thông tin tài khoản đăng nhập";
        String template = "send-mail-client-template";
        sendMailDomain.setSubject(subject);
        Map<String, Object> paramInfo = new HashMap<>();
        paramInfo.put("email", email);
        name = StringUtils.isEmpty(name) ? "bạn" : name;
        paramInfo.put("username", name);
        paramInfo.put("password", password);
        paramInfo.put("linkCms", link);
        sendMailUtils.sendMailWithTemplate(sendMailDomain, template, paramInfo);
    }


    @Override
    public ResponseDataAPI getListTask(GetListRequest request) {

        List<String> columnSort = Arrays.asList(ColumnSortTask.NAME.getName(), ColumnSortTask.START_TIME.getName(), ColumnSortTask.WORK_DATE.getName(), ColumnSortTask.START_TIME.getName(), ColumnSortTask.END_TIME.getName());
        Pageable pageable = null;
        if (columnSort.contains(request.getColumnSort())) {
            if (ColumnSortTask.NAME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.NAME.getValue());
            } else if (ColumnSortTask.START_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.START_TIME.getValue());
            } else if (ColumnSortTask.END_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.END_TIME.getValue());
            } else if (ColumnSortTask.WORK_DATE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.WORK_DATE.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortTask.CREATED_DATE.getValue()).descending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch()).toUpperCase();
        String status = request.getStatus();
        Page<Object[]> entities;
        if (StringUtils.isEmpty(status)) {
            entities = taskRepository.findAllWithPageable(valueSearch, pageable);
        } else if (TaskStatus.ASSIGNED.getValue().equals(status)) {
            entities = taskRepository.findAssignedWithPageable(valueSearch, pageable);
        } else if (TaskStatus.UN_ASSIGNED.getValue().equals(status)) {
            entities = taskRepository.findUnAssignedWithPageable(valueSearch, pageable);
        } else if (TaskStatus.CANCELED.getValue().equals(status)) {
            entities = taskRepository.findCanceledWithPageable(valueSearch, pageable);
        } else if (TaskStatus.DONE.getValue().equals(status)) {
            entities = taskRepository.findPaidWithPageable(valueSearch, pageable);
        } else {
            entities = taskRepository.findAllWithPageable(valueSearch, pageable);
        }

        List<Object> result = entities.stream().map(objects -> {
            GetListTaskDomain domain = new GetListTaskDomain();
            TaskEntity taskEntity = (TaskEntity) objects[0];
            domain.setUserAvatar(StringUtils.convertObjectToString(objects[1]));
            domain.setUserName(StringUtils.convertObjectToString(objects[2]));
            domain.setId(taskEntity.getId().toString());
            domain.setAddress(taskEntity.getAddress());
            domain.setStartTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getStartTime(), DateTimeUtils.hhmmss));
            domain.setEndTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getCompleteTime(), DateTimeUtils.hhmmss));
            domain.setWorkDate(StringUtils.convertDateToStringFormatPattern(taskEntity.getWorkDate(), DateTimeUtils.DDMMYYYY));
            List<MapDomain> listTimeStatus = Arrays.asList(new MapDomain(TaskStatus.CREATED.getValue(), taskEntity.getCreatedDate()),
                    new MapDomain(TaskStatus.CANCELED.getValue(), taskEntity.getCancelTime()),
                    new MapDomain(TaskStatus.ASSIGNED.getValue(), taskEntity.getAssignEmployeeTime()),
                    new MapDomain(TaskStatus.DONE.getValue(), taskEntity.getPaidTime()));
            listTimeStatus = listTimeStatus.stream().filter(t -> t.getValue() != null).sorted(Comparator.nullsLast(
                    (e1, e2) -> e2.getValue().compareTo(e1.getValue()))).collect(Collectors.toList());
            domain.setStatus(listTimeStatus.get(0).getKey());
            return domain;
        }).collect(Collectors.toList());

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(result);
        responseDataAPI.setTotalRows(entities.getTotalElements());
        return responseDataAPI;
    }

    @Override
    public ResponseDataAPI getListTaskOfUser(GetListRequest request) {
        Pageable pageable = null;
        pageable = getPageable(request, pageable);

        String status = request.getStatus();
        Page<TaskEntity> entities;
        Long currenUser = UserUtils.getCurrentUserId();
        if (StringUtils.isEmpty(status)) {
            entities = taskRepository.findAllOfUserWithPageable(currenUser, pageable);
        } else if (TaskStatus.ASSIGNED.getValue().equals(status)) {
            entities = taskRepository.findAssignedOfUserWithPageable(currenUser, pageable);
        } else if (TaskStatus.UN_ASSIGNED.getValue().equals(status)) {
            entities = taskRepository.findUnAssignedOfUserWithPageable(currenUser, pageable);
        } else if (TaskStatus.CANCELED.getValue().equals(status)) {
            entities = taskRepository.findCanceledOfUserWithPageable(currenUser, pageable);
        } else if (TaskStatus.DONE.getValue().equals(status)) {
            entities = taskRepository.findPaidOfUserWithPageable(currenUser, pageable);
        } else {
            entities = taskRepository.findAllOfUserWithPageable(currenUser, pageable);
        }

        List<Object> result = entities.stream().map(taskEntity -> {
            GetListTaskUserDomain domain = new GetListTaskUserDomain();
            domain.setId(taskEntity.getId().toString());
            domain.setAddress(taskEntity.getAddress());
            domain.setStartTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getStartTime(), DateTimeUtils.hhmmss));
            domain.setEndTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getCompleteTime(), DateTimeUtils.hhmmss));
            domain.setWorkDate(StringUtils.convertDateToStringFormatPattern(taskEntity.getWorkDate(), DateTimeUtils.DDMMYYYY));
            List<MapDomain> listTimeStatus = Arrays.asList(new MapDomain(TaskStatus.CREATED.getValue(), taskEntity.getCreatedDate()),
                    new MapDomain(TaskStatus.CANCELED.getValue(), taskEntity.getCancelTime()),
                    new MapDomain(TaskStatus.ASSIGNED.getValue(), taskEntity.getAssignEmployeeTime()),
                    new MapDomain(TaskStatus.DONE.getValue(), taskEntity.getPaidTime()));
            listTimeStatus = listTimeStatus.stream().filter(t -> t.getValue() != null).sorted(Comparator.nullsLast(
                    (e1, e2) -> e2.getValue().compareTo(e1.getValue()))).collect(Collectors.toList());
            domain.setStatus(listTimeStatus.get(0).getKey());
            if (taskEntity.getEmployeeId() == null) {
                domain.setEmployeeAvatar(new ArrayList<>());
            } else {
                UserEntity employee = userRepository.findById(taskEntity.getEmployeeId()).orElse(null);
                domain.setEmployeeAvatar(employee == null ? new ArrayList<>() : Arrays.asList(employee.getAvatar()));
            }
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
                    Sort.by(Sort.Order.desc(ColumnSortTask.CREATED_DATE.getValue())));
        }
        return pageable;
    }

    @Override
    public ResponseDataAPI getListTaskOfEmployee(GetListRequest request, String date) {
        List<String> columnSort = Arrays.asList(ColumnSortTask.NAME.getName(), ColumnSortTask.START_TIME.getName(), ColumnSortTask.WORK_DATE.getName(), ColumnSortTask.START_TIME.getName(), ColumnSortTask.END_TIME.getName());
        Pageable pageable = null;
        if (columnSort.contains(request.getColumnSort())) {
            if (ColumnSortTask.NAME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.NAME.getValue());
            } else if (ColumnSortTask.START_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.START_TIME.getValue());
            } else if (ColumnSortTask.END_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.END_TIME.getValue());
            } else if (ColumnSortTask.WORK_DATE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortTask.WORK_DATE.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortTask.CREATED_DATE.getValue()).descending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch()).toUpperCase();
        Long employeeId = UserUtils.getCurrentUserId();
        Date workDate = DateTimeUtils.convertStringToDateOrNull(date, DateTimeUtils.YYYYMMDD);
        Page<Object[]> entities = taskRepository.findAllOfEmployeeWithPageable(employeeId, valueSearch, workDate, pageable);


        List<Object> result = entities.stream().map(objects -> {
            GetListTaskDomain domain = new GetListTaskDomain();
            TaskEntity taskEntity = (TaskEntity) objects[0];
            domain.setUserAvatar(StringUtils.convertObjectToString(objects[1]));
            domain.setUserName(StringUtils.convertObjectToString(objects[2]));
            domain.setId(taskEntity.getId().toString());
            domain.setAddress(taskEntity.getAddress());
            domain.setStartTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getStartTime(), DateTimeUtils.hhmmss));
            domain.setEndTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getCompleteTime(), DateTimeUtils.hhmmss));
            domain.setWorkDate(StringUtils.convertDateToStringFormatPattern(taskEntity.getWorkDate(), DateTimeUtils.DDMMYYYY));
            List<MapDomain> listTimeStatus = Arrays.asList(new MapDomain(TaskStatus.CREATED.getValue(), taskEntity.getCreatedDate()),
                    new MapDomain(TaskStatus.CANCELED.getValue(), taskEntity.getCancelTime()),
                    new MapDomain(TaskStatus.ASSIGNED.getValue(), taskEntity.getAssignEmployeeTime()),
                    new MapDomain(TaskStatus.DONE.getValue(), taskEntity.getPaidTime()));
            listTimeStatus = listTimeStatus.stream().filter(t -> t.getValue() != null).sorted(Comparator.nullsLast(
                    (e1, e2) -> e2.getValue().compareTo(e1.getValue()))).collect(Collectors.toList());
            domain.setStatus(listTimeStatus.get(0).getKey());
            return domain;
        }).collect(Collectors.toList());

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(result);
        responseDataAPI.setTotalRows(entities.getTotalElements());
        return responseDataAPI;
    }

    @Override
    public List<String> getListWorkDateOfEmployee() {
        Long userId = UserUtils.getCurrentUserId();
        List<TaskEntity> taskEntities = taskRepository.findByEmployeeId(userId);
        List<String> workDate = taskEntities.stream().map(t -> StringUtils.convertDateToStringFormatPattern(t.getWorkDate(), DateTimeUtils.YYYYMMDD)).collect(Collectors.toList());
        return workDate;
    }

    @Override
    public TaskDetailDomain getTaskDetail(String taskId) {
        Long task = StringUtils.convertObjectToLongOrNull(taskId);
        TaskEntity taskEntity = taskRepository.findById(task).orElse(null);
        UserEntity clientEntity = userRepository.findById(taskEntity.getUserId()).orElse(null);
        TaskDetailDomain taskDetailDomain = new TaskDetailDomain();
        taskDetailDomain.setWorkDate(StringUtils.convertDateToStringFormatyyyyMMdd(taskEntity.getWorkDate()));
        taskDetailDomain.setNote(taskEntity.getNote());
//        taskDetailDomain.setNumOfEmployee(taskEntity.getNumberUser() == null ? "0" : taskEntity.getNumberUser().toString());
        ClientInformDomain clientInform = new ClientInformDomain();
        clientInform.setAvatar(clientEntity.getAvatar());
        clientInform.setEmail(clientEntity.getEmail());
        clientInform.setPhone(clientEntity.getPhoneNumber());
        clientInform.setFullName(clientEntity.getFullName());
        taskDetailDomain.setClientInform(clientInform);
        taskDetailDomain.setServiceId(StringUtils.convertObjectToString(taskEntity.getServiceId()));
        taskDetailDomain.setEndTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getCompleteTime(), "HH:mm"));
        taskDetailDomain.setStartTime(StringUtils.convertDateToStringFormatPattern(taskEntity.getStartTime(), "HH:mm"));
        taskDetailDomain.setAddress(taskEntity.getAddress());
        CheckPriceResponseDomain checkPriceResponseDomain = new CheckPriceResponseDomain();

        List<PriceListDomain> priceList = new ArrayList<>();
        Period p = new Period(new DateTime(taskEntity.getStartTime()), new DateTime(taskEntity.getCompleteTime()));
        int hours = p.getHours();
        int minutes = p.getMinutes();
        double totalHours = hours + (double) minutes / 60;
        List<Long> serviceTaskId = Arrays.asList(taskEntity.getServiceId());

        for (Long serviceId : serviceTaskId) {
            ServiceCompanyEntity service = serviceCompanyRepository.findById(serviceId).orElse(null);
            Long serviceDiscount = taskEntity.getDiscountServiceId();

            PriceListDomain item = new PriceListDomain();
            item.setHours(new DecimalFormat("0").format(totalHours));
            item.setServiceName(service.getServiceName());
            item.setServicePrice(new DecimalFormat("0").format(service.getPrice()));
            if (serviceDiscount != null) {
                ServiceDiscountEntity serviceDiscountEntity = serviceDiscountRepository.findById(serviceDiscount).orElse(null);
                DiscountEntity discountEntity = discountRepository.findById(serviceDiscountEntity.getDiscountId()).orElse(null);
                item.setDiscountApply(discountEntity == null ? "" : discountEntity.getTitle());
                item.setPercentApply(serviceDiscountEntity.getSalePercentage().toString());
                double price = service.getPrice() * totalHours * (100 - serviceDiscountEntity.getSalePercentage()) / 100;
                item.setPrice(new DecimalFormat("0").format(price));
            } else {
                double price = service.getPrice() * totalHours;
                item.setPrice(new DecimalFormat("0").format(price));
            }
            priceList.add(item);
        }
        checkPriceResponseDomain.setPriceList(priceList);
        double total = priceList.stream().map(t -> Double.valueOf(t.getPrice())).reduce((t1, t2) -> Double.sum(t1, t2)).get();
        checkPriceResponseDomain.setTotal(new DecimalFormat("00.00").format(total));

        List<UserInformDomain> employees = new ArrayList<>();
        List<UserEntity> userEntities = new ArrayList<>();
        if (taskEntity.getEmployeeId() != null) {
            UserEntity employee = userRepository.findById(taskEntity.getEmployeeId()).orElse(null);
            userEntities = Arrays.asList(employee);
        }
        employees = userEntities.stream().map(t -> {
            UserInformDomain domain = new UserInformDomain();
            domain.setId(t.getId().toString());
            domain.setFullName(t.getFullName());
            domain.setAvatar(t.getAvatar());
            domain.setPhone(t.getPhoneNumber());
            EmployeeServiceEntity employeeServiceEntity = employeeServiceRepository.findByServiceIdAndAndUserId(serviceTaskId.get(0), t.getId());
            domain.setNumStar(StringUtils.convertObjectToString(employeeServiceEntity.getLevel()));
            Integer numTask = taskRepository.findByEmployeeId(t.getId()).size();
            domain.setNumTask(StringUtils.convertObjectToString(numTask));
            return domain;
        }).collect(Collectors.toList());

        List<MapDomain> listTimeStatus = Arrays.asList(new MapDomain(TaskProgress.CREATED.getValue(), taskEntity.getCreatedDate()),
                new MapDomain(TaskProgress.CANCELED.getValue(), taskEntity.getCancelTime()),
                new MapDomain(TaskProgress.ASSIGNED.getValue(), taskEntity.getAssignEmployeeTime()),
                new MapDomain(TaskProgress.DONE.getValue(), taskEntity.getPaidTime()));
        listTimeStatus = listTimeStatus.stream().filter(t -> t.getValue() != null).sorted(Comparator.nullsLast(
                (e1, e2) -> e2.getValue().compareTo(e1.getValue()))).collect(Collectors.toList());
        List<TaskProgressDomain> progress = listTimeStatus.stream().map(t -> {
            TaskProgressDomain domain = new TaskProgressDomain();
            domain.setStatus(t.getKey());
            domain.setTime(StringUtils.convertDateToStringFormatPattern(t.getValue(), DateTimeUtils.YYYYMMDDhhmmss));
            return domain;
        }).collect(Collectors.toList());
        taskDetailDomain.setProgress(progress);
        checkPriceResponseDomain.setEmployees(employees);
        taskDetailDomain.setPriceList(checkPriceResponseDomain);
        return taskDetailDomain;
    }


    @Override
    public List<UserInformDomain> getListUserAvailableWithTaskTime(String startTime, String endTime, String serviceId, String taskId) {
        Long service = StringUtils.convertStringToLongOrNull(serviceId);
        Date startTimeCheck = DateTimeUtils.convertStringToDateOrNull(startTime, DateTimeUtils.YYYYMMDDhhmmss);
        Date endTimeCheck = DateTimeUtils.convertStringToDateOrNull(endTime, DateTimeUtils.YYYYMMDDhhmmss);
        Date workDate = DateTimeUtils.convertStringToDateOrNull(startTime, DateTimeUtils.YYYYMMDD);
        List<LeaveDateEntity> leaveDateEntities = leaveDateRepository.findLeaveDateOnDate(workDate);

        List<Long> unAvailableUser = new ArrayList<>();
        for (LeaveDateEntity entity : leaveDateEntities) {
            Date start = entity.getStart();
            Date end = entity.getEnd();
            if (endTimeCheck.getTime() < start.getTime() || startTimeCheck.getTime() < end.getTime()) {
                unAvailableUser.add(entity.getUserId());
            }
        }

        List<TaskEntity> taskEntities = taskRepository.findTaskOnDate(workDate);
        taskEntities.stream().filter(t ->  t.getCancelTime() == null || t.getPaidTime() == null );
        Long task = StringUtils.convertObjectToLongOrNull(taskId);
        for (TaskEntity taskEntity : taskEntities) {
            Date start = taskEntity.getStartTime();
            Date end = taskEntity.getCompleteTime();
            if (task != taskEntity.getId() && (endTimeCheck.getTime() < start.getTime() || startTimeCheck.getTime() < end.getTime())) {
                if (taskEntity.getEmployeeId() != null) {
                    unAvailableUser.add(taskEntity.getEmployeeId());
                }
            }
        }

        List<Long> unAvailableUserIds = unAvailableUser.stream().distinct().collect(Collectors.toList());
        List<UserEntity> userEntities;
        if (!CollectionUtils.isEmpty(unAvailableUserIds)) {
            userEntities = userRepository.findUserNotInListIds(unAvailableUserIds);
        } else {
            userEntities = userRepository.findUserHasStatusNotBlock();
        }
        List<Long> listEmployeeHasEperience = employeeServiceRepository.findByServiceId(service).stream().map(t -> t.getUserId()).collect(Collectors.toList());
        List<UserInformDomain> response = userEntities.stream().filter(t -> listEmployeeHasEperience.contains(t.getId())).map(t -> {
            UserInformDomain domain = new UserInformDomain();
            domain.setId(t.getId().toString());
            domain.setAvatar(t.getAvatar());
            EmployeeServiceEntity employeeServiceEntity = employeeServiceRepository.findByServiceIdAndAndUserId(service, t.getId());
            domain.setNumStar(StringUtils.convertObjectToString(employeeServiceEntity.getLevel()));
            Integer numTask = taskRepository.findByEmployeeId(t.getId()).size();
            domain.setNumTask(StringUtils.convertObjectToString(numTask));
            domain.setFullName(t.getFullName());
            domain.setPhone(t.getPhoneNumber());

            return domain;
        }).collect(Collectors.toList());
        return response;
    }

    @Transactional
    @Override
    public void assignEmployeeToTask(AssignTaskDomain assignTaskDomain) {
        List<String> ids = assignTaskDomain.getIds();
        List<Long> id = ids.stream().map(t -> StringUtils.convertObjectToLongOrNull(t)).collect(Collectors.toList());
        Long task = StringUtils.convertObjectToLongOrNull(assignTaskDomain.getTaskId());
        TaskEntity taskEntity = taskRepository.findById(task).orElse(null);
        taskEntity.setEmployeeId(id.get(0));
        UserEntity userEntity = userRepository.findById(id.get(0)).orElse(null);
        taskEntity.setAssignEmployeeTime(new Date());
        taskRepository.save(taskEntity);
        sendMailToEmployee(userEntity.getFullName(), StringUtils.convertDateToStringFormatPattern(taskEntity.getWorkDate(), DateTimeUtils.YYYYMMDD), userEntity.getEmail());
    }

    void sendMailToEmployee(String employeeName, String date, String employeeEmail) {
        SendMailDomain sendMailDomain = new SendMailDomain();
        sendMailDomain.setToEmail(Arrays.asList(employeeEmail));
        sendMailDomain.setMessageContent("");
        String subject = "Công việc mới tại CleanMe";
        String template = "send-mail-employee-template";
        sendMailDomain.setSubject(subject);
        Map<String, Object> paramInfo = new HashMap<>();
        paramInfo.put("date", date);
        paramInfo.put("username", employeeName);
        paramInfo.put("linkCms", "https://hourly-maid-service-employee.herokuapp.com/");
        sendMailUtils.sendMailWithTemplate(sendMailDomain, template, paramInfo);
    }

    @Override
    public void upDateTaskInform(UpdateTaskInform updateTaskInform) {
        Long task = StringUtils.convertObjectToLongOrNull(updateTaskInform.getTaskId());
        TaskEntity taskEntity = taskRepository.findById(task).orElse(null);
        if (StringUtils.isEmpty(updateTaskInform.getAddress())) {
            throw new CustomException("Địa chỉ trống, vui lòng thử lại!", "S0000", HttpStatus.BAD_REQUEST);
        }
        taskEntity.setNote(updateTaskInform.getNote());
        taskEntity.setAddress(updateTaskInform.getAddress());
        taskRepository.save(taskEntity);
    }

    @Override
    public void cancelTask(ChangeNotifyStatusDomain cancelTaskInform) {
        Long id = StringUtils.convertObjectToLongOrNull(cancelTaskInform.getId());
        if (id == null) {
            throw new CustomException("Không tìm thấy công việc!", Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        TaskEntity taskEntity = taskRepository.findById(id).orElse(null);
        if (taskEntity == null) {
            throw new CustomException("Không tìm thấy công việc!", Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        taskEntity.setCancelTime(new Date());
        taskRepository.save(taskEntity);
    }

    @Override
    public void doneTask(ChangeNotifyStatusDomain doneDomain) {
        Long id = StringUtils.convertObjectToLongOrNull(doneDomain.getId());
        if (id == null) {
            throw new CustomException("Không tìm thấy công việc!", Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        TaskEntity taskEntity = taskRepository.findById(id).orElse(null);
        if (taskEntity == null) {
            throw new CustomException("Không tìm thấy công việc!", Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        taskEntity.setPaidTime(new Date());
        taskRepository.save(taskEntity);
    }

    @Override
    public OverviewTaskDomain getTaskOverviewDetail(String statDate, String endDate) {
        OverviewTaskDomain overviewTaskDomain = new OverviewTaskDomain();
        Date start = DateTimeUtils.convertStringToDateOrNull(statDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<TaskEntity> taskEntities = taskRepository.findAll();

        Date today = new Date();
        String oneDayAgoString = StringUtils.convertDateToStringFormatPattern(new Date(today.getTime() - 86400000), DateTimeUtils.YYYYMMDD);
        Date oneDayAgo = DateTimeUtils.convertStringToDateOrNull(oneDayAgoString, DateTimeUtils.YYYYMMDD);

        String twoDayAgoString = StringUtils.convertDateToStringFormatPattern(new Date(today.getTime() - 2 * 86400000), DateTimeUtils.YYYYMMDD);
        Date twoDayAgo = DateTimeUtils.convertStringToDateOrNull(twoDayAgoString, DateTimeUtils.YYYYMMDD);
        overviewTaskDomain.setNumCreate(StringUtils.convertObjectToString(taskEntities.size()));
        List<TaskEntity> taskCreateTwoDayAgo = taskEntities.stream().filter(t -> t.getWorkDate().getTime() == twoDayAgo.getTime()).collect(Collectors.toList());
        List<TaskEntity> taskCreateOneDayAgo = taskEntities.stream().filter(t -> t.getWorkDate().getTime() == oneDayAgo.getTime()).collect(Collectors.toList());

        String percentCreate;
        if (taskCreateTwoDayAgo.size() == 0) {
            percentCreate = new DecimalFormat("0.00").format(taskCreateOneDayAgo.size() * 100);
        } else {
            percentCreate = new DecimalFormat("0.00").format((taskCreateOneDayAgo.size() - taskCreateTwoDayAgo.size()) / taskCreateTwoDayAgo.size() * 100 / 100);
        }
        overviewTaskDomain.setPercentCreate(percentCreate);
        List<TaskEntity> taskCancel = taskEntities.stream().filter(t -> t.getCancelTime() != null).collect(Collectors.toList());
        List<TaskEntity> taskCancelTwoDayAgo = taskCreateTwoDayAgo.stream().filter(t -> {
            String cancelDateString = StringUtils.convertDateToStringFormatPattern(t.getCancelTime(), DateTimeUtils.YYYYMMDD);
            Date cancelDate = DateTimeUtils.convertStringToDateOrNull(cancelDateString, DateTimeUtils.YYYYMMDD);
            return t.getCancelTime() != null && cancelDate.getTime() == twoDayAgo.getTime();
        }).collect(Collectors.toList());
        List<TaskEntity> taskCancelOneDayAgo = taskCreateOneDayAgo.stream().filter(t -> {
            String cancelDateString = StringUtils.convertDateToStringFormatPattern(t.getCancelTime(), DateTimeUtils.YYYYMMDD);
            Date cancelDate = DateTimeUtils.convertStringToDateOrNull(cancelDateString, DateTimeUtils.YYYYMMDD);
            return t.getCancelTime() != null && cancelDate.getTime() == oneDayAgo.getTime();
        }).collect(Collectors.toList());
        String percentCancel;
        if (taskCancelTwoDayAgo.size() == 0) {
            percentCancel = new DecimalFormat("00.00").format(taskCancelOneDayAgo.size() * 100);
        } else {
            percentCancel = new DecimalFormat("00.00").format((taskCancelOneDayAgo.size() - taskCancelTwoDayAgo.size()) / taskCancelTwoDayAgo.size() * 100 / 100);
        }
        overviewTaskDomain.setNumCancel(StringUtils.convertObjectToString(taskCancel.size()));
        overviewTaskDomain.setPercentCancel(percentCancel);

        List<TaskEntity> taskDone = taskEntities.stream().filter(t -> t.getPaidTime() != null).collect(Collectors.toList());
        List<TaskEntity> taskDoneTwoDayAgo = taskCreateTwoDayAgo.stream().filter(t -> t.getPaidTime() != null).collect(Collectors.toList());
        List<TaskEntity> taskDoneOneDayAgo = taskCreateOneDayAgo.stream().filter(t -> t.getPaidTime() != null).collect(Collectors.toList());
        String percentDone;
        if (taskDoneTwoDayAgo.size() == 0) {
            percentDone = new DecimalFormat("00.00").format(taskDoneOneDayAgo.size() * 100);
        } else {
            percentDone = new DecimalFormat("00.00").format((taskDoneOneDayAgo.size() - taskDoneTwoDayAgo.size()) / taskDoneTwoDayAgo.size() * 100 / 100);
        }
        overviewTaskDomain.setNumDone(StringUtils.convertObjectToString(taskDone.size()));
        overviewTaskDomain.setPercentDone(percentDone);

        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);

        List<ItemOnDateDomain> details = new ArrayList<>();
        for (Date date : dateList) {
            Integer taskOnDate = taskEntities.stream().filter(t -> t.getWorkDate().getTime() == date.getTime()).collect(Collectors.toList()).size();
            ItemOnDateDomain domain = new ItemOnDateDomain();
            domain.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
            domain.setNumber(StringUtils.convertObjectToString(taskOnDate));
            details.add(domain);
        }
        overviewTaskDomain.setDetails(details);

        return overviewTaskDomain;
    }

    @Override
    public List<ItemOnDateDomain> getOveriewDetailOfTask(String statDate, String endDate) {
        Date start = DateTimeUtils.convertStringToDateOrNull(statDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<TaskEntity> taskEntities = taskRepository.findAll();
        List<ItemOnDateDomain> details = new ArrayList<>();
        for (Date date : dateList) {
            Integer taskOnDate = taskEntities.stream().filter(t -> t.getWorkDate().getTime() == date.getTime()).collect(Collectors.toList()).size();
            ItemOnDateDomain domain = new ItemOnDateDomain();
            domain.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
            domain.setNumber(StringUtils.convertObjectToString(taskOnDate));
            details.add(domain);
        }
        return details;
    }

    @Override
    public OverviewTaskDomain getTaskOverviewDetailForEmployee(String statDate, String endDate) {
        Long employeeId = UserUtils.getCurrentUserId();
        OverviewTaskDomain overviewTaskDomain = new OverviewTaskDomain();
        Date start = DateTimeUtils.convertStringToDateOrNull(statDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<TaskEntity> taskEntities = taskRepository.findByEmployeeId(employeeId);

        Date today = new Date();
        String oneDayAgoString = StringUtils.convertDateToStringFormatPattern(new Date(today.getTime() - 86400000), DateTimeUtils.YYYYMMDD);
        Date oneDayAgo = DateTimeUtils.convertStringToDateOrNull(oneDayAgoString, DateTimeUtils.YYYYMMDD);

        String twoDayAgoString = StringUtils.convertDateToStringFormatPattern(new Date(today.getTime() - 2 * 86400000), DateTimeUtils.YYYYMMDD);
        Date twoDayAgo = DateTimeUtils.convertStringToDateOrNull(twoDayAgoString, DateTimeUtils.YYYYMMDD);
        overviewTaskDomain.setNumCreate(StringUtils.convertObjectToString(taskEntities.size()));
        List<TaskEntity> taskCreateTwoDayAgo = taskEntities.stream().filter(t -> {
            String workDateString = StringUtils.convertDateToStringFormatPattern(t.getWorkDate(), DateTimeUtils.YYYYMMDD);
            return DateTimeUtils.convertStringToDateOrNull(workDateString, DateTimeUtils.YYYYMMDD).getTime() == twoDayAgo.getTime();
        }).collect(Collectors.toList());
        List<TaskEntity> taskCreateOneDayAgo = taskEntities.stream().filter(t -> {
            String workDateString = StringUtils.convertDateToStringFormatPattern(t.getWorkDate(), DateTimeUtils.YYYYMMDD);
            return DateTimeUtils.convertStringToDateOrNull(workDateString, DateTimeUtils.YYYYMMDD).getTime() == oneDayAgo.getTime();
        }).collect(Collectors.toList());

        String percentCreate;
        if (taskCreateTwoDayAgo.size() == 0) {
            percentCreate = new DecimalFormat("00.00").format(taskCreateOneDayAgo.size() * 100);
        } else {
            percentCreate = new DecimalFormat("00.00").format((taskCreateOneDayAgo.size() - taskCreateTwoDayAgo.size()) / taskCreateTwoDayAgo.size() * 100 / 100);
        }
        overviewTaskDomain.setPercentCreate(percentCreate);
        List<TaskEntity> taskCancel = taskEntities.stream().filter(t -> t.getCancelTime() != null).collect(Collectors.toList());
        List<TaskEntity> taskCancelTwoDayAgo = taskCreateTwoDayAgo.stream().filter(t -> {
            String cancelDateString = StringUtils.convertDateToStringFormatPattern(t.getCancelTime(), DateTimeUtils.YYYYMMDD);
            Date cancelDate = DateTimeUtils.convertStringToDateOrNull(cancelDateString, DateTimeUtils.YYYYMMDD);
            return t.getCancelTime() != null && cancelDate.getTime() == twoDayAgo.getTime();
        }).collect(Collectors.toList());
        List<TaskEntity> taskCancelOneDayAgo = taskCreateOneDayAgo.stream().filter(t -> {
            String cancelDateString = StringUtils.convertDateToStringFormatPattern(t.getCancelTime(), DateTimeUtils.YYYYMMDD);
            Date cancelDate = DateTimeUtils.convertStringToDateOrNull(cancelDateString, DateTimeUtils.YYYYMMDD);
            return t.getCancelTime() != null && cancelDate.getTime() == oneDayAgo.getTime();
        }).collect(Collectors.toList());
        String percentCancel;
        if (taskCancelTwoDayAgo.size() == 0) {
            percentCancel = new DecimalFormat("00.00").format(taskCancelOneDayAgo.size() * 100);
        } else {
            percentCancel = new DecimalFormat("00.00").format((taskCancelOneDayAgo.size() - taskCancelTwoDayAgo.size()) / taskCancelTwoDayAgo.size() * 100 / 100);
        }
        overviewTaskDomain.setNumCancel(StringUtils.convertObjectToString(taskCancel.size()));
        overviewTaskDomain.setPercentCancel(percentCancel);

        List<TaskEntity> taskDone = taskEntities.stream().filter(t -> t.getPaidTime() != null).collect(Collectors.toList());
        List<TaskEntity> taskDoneTwoDayAgo = taskCreateTwoDayAgo.stream().filter(t -> t.getPaidTime() != null).collect(Collectors.toList());
        List<TaskEntity> taskDoneOneDayAgo = taskCreateOneDayAgo.stream().filter(t -> t.getPaidTime() != null).collect(Collectors.toList());
        String percentDone;
        if (taskDoneTwoDayAgo.size() == 0) {
            percentDone = new DecimalFormat("00.00").format(taskDoneOneDayAgo.size() * 100);
        } else {
            percentDone = new DecimalFormat("00.00").format((taskDoneOneDayAgo.size() - taskDoneTwoDayAgo.size()) / taskDoneTwoDayAgo.size() * 100 / 100);
        }
        overviewTaskDomain.setNumDone(StringUtils.convertObjectToString(taskDone.size()));
        overviewTaskDomain.setPercentDone(percentDone);

        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);

        List<ItemOnDateDomain> details = new ArrayList<>();
        for (Date date : dateList) {
            Integer taskOnDate = taskEntities.stream().filter(t -> t.getWorkDate().getTime() == date.getTime()).collect(Collectors.toList()).size();
            ItemOnDateDomain domain = new ItemOnDateDomain();
            domain.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
            domain.setNumber(StringUtils.convertObjectToString(taskOnDate));
            details.add(domain);
        }
        overviewTaskDomain.setDetails(details);

        return overviewTaskDomain;
    }

    @Override
    public List<ItemOnDateDomain> getOveriewDetailOfTaskForEmployee(String statDate, String endDate) {
        Date start = DateTimeUtils.convertStringToDateOrNull(statDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<TaskEntity> taskEntities = taskRepository.findByEmployeeId(UserUtils.getCurrentUserId());
        List<ItemOnDateDomain> details = new ArrayList<>();
        for (Date date : dateList) {
            Integer taskOnDate = taskEntities.stream().filter(t -> t.getWorkDate().getTime() == date.getTime()).collect(Collectors.toList()).size();
            ItemOnDateDomain domain = new ItemOnDateDomain();
            domain.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
            domain.setNumber(StringUtils.convertObjectToString(taskOnDate));
            details.add(domain);
        }
        return details;
    }
}
