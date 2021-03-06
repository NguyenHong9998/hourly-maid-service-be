package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.constant.*;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.entity.*;
import com.example.hourlymaids.repository.EmployeeServiceRepository;
import com.example.hourlymaids.repository.ServiceCompanyRepository;
import com.example.hourlymaids.repository.ServiceDiscountRepository;
import com.example.hourlymaids.repository.TaskRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceCompanyServiceImpl implements ServiceCompanyService {
    @Autowired
    private ServiceCompanyRepository serviceCompanyRepository;

    @Autowired
    private ServiceDiscountRepository serviceDiscountRepository;

    @Autowired
    private EmployeeServiceRepository employeeServiceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public ResponseDataAPI getListService(GetListRequest request) {
        Integer offset = (request.getOffset() == null || request.getOffset() < 1) ? 0 : request.getOffset() - 1;
        Integer limit = (request.getLimit() == null || request.getLimit() < 1) ? 10 : request.getLimit();

        request.setLimit(limit);
        request.setOffset(offset);

        List<String> columnSort = Arrays.asList(ColumnSortService.NAME.getName(), ColumnSortService.PRICE.getName(), ColumnSortService.NOTE.getName(), ColumnSortService.PRICE.getName());
        Pageable pageable = null;

        if (columnSort.contains(request.getColumnSort())) {
            if (ColumnSortService.NAME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortService.NAME.getValue());
            } else if (ColumnSortService.PRICE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortService.NAME.getValue());
            } else if (ColumnSortService.NOTE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortService.NOTE.getValue());
            } else if (ColumnSortService.CREATED_DATE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortService.CREATED_DATE.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortService.CREATED_DATE.getValue()).descending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch()).toUpperCase();
        Page<ServiceCompanyEntity> entities;
        String status = request.getStatus();
        if (StringUtils.isEmpty(status)) {
            entities = serviceCompanyRepository.findAllService(valueSearch, pageable);
        } else {
            Integer statusService = ServiceStatus.getEmployeeStatusByValue(request.getStatus()).getCode();
            entities = serviceCompanyRepository.findAllServiceWithStatus(statusService, valueSearch, pageable);
        }


        List<Object> result = entities.stream().map(service -> {
            ServiceDomain serviceDomain = new ServiceDomain();
            Integer numTask = taskRepository.findByServiceId(service.getId()).size();
            serviceDomain.setNumTask(StringUtils.convertObjectToString(numTask));
            List<Object[]> serviceDiscountEntities = serviceDiscountRepository.findDiscountByServiceId(service.getId());
            List<DiscountDomain> discountDomains = serviceDiscountEntities.stream().map(t -> {
                DiscountDomain discountDomain = new DiscountDomain();
                DiscountEntity discountEntity = (DiscountEntity) t[0];
                discountDomain.setBanner(discountEntity.getBanner());
                discountDomain.setTitle(discountEntity.getTitle());
                discountDomain.setId(StringUtils.convertObjectToString(discountEntity.getId()));
                discountDomain.setEndTime(DateTimeUtils.convertDateToStringOrEmpty(discountEntity.getEndTime(), DateTimeUtils.YYYYMMDDhhmmss));
                discountDomain.setSalePercentage(StringUtils.convertObjectToString(t[1]));
                return discountDomain;
            }).collect(Collectors.toList());
            serviceDomain.setDiscounts(discountDomains);
            serviceDomain.setId(service.getId().toString());
            serviceDomain.setName(service.getServiceName());
            serviceDomain.setPrice(StringUtils.convertObjectToString(service.getPrice()));
            serviceDomain.setNote(service.getNote());
            serviceDomain.setBanner(service.getBanner());
            serviceDomain.setAdvantage(service.getAdvantages());
            serviceDomain.setStatus(ServiceStatus.getServiceStatusByCode(service.getStatus()).getValue());
            serviceDomain.setIntroduces(service.getIntroduce());
            serviceDomain.setCreateDate(StringUtils.convertDateToStringFormatyyyyMMdd(service.getCreatedDate()));
            return serviceDomain;
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
                    Sort.by(Sort.Order.desc(ColumnSortService.NAME.getValue())));
        }
        return pageable;
    }

    @Override
    public ServiceDomain getServiceDetail(String serviceId) {
        Long id = StringUtils.convertStringToLongOrNull(serviceId);
        if (id == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        ServiceCompanyEntity entity = serviceCompanyRepository.getById(id);
        if (entity == null) {
            throw new CustomException(Error.SERVICES_NOT_EXIST.getMessage(), Error.SERVICES_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        ServiceDomain serviceDomain = new ServiceDomain();
        serviceDomain.setBanner(entity.getBanner());
        serviceDomain.setNote(entity.getNote());
        serviceDomain.setPrice(StringUtils.convertObjectToString(entity.getPrice()));
        serviceDomain.setId(entity.getId().toString());
        serviceDomain.setName(entity.getServiceName());
        serviceDomain.setAdvantage(entity.getAdvantages());
        serviceDomain.setIntroduces(entity.getIntroduce());

        return serviceDomain;
    }

    @Override
    public void updateService(String serviceId, ServiceDomain domain) {
        Long id = StringUtils.convertStringToLongOrNull(serviceId);
        if (id == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isEmpty(domain.getName()) || StringUtils.isEmpty(domain.getPrice())) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.getById(id);
        if (serviceCompanyEntity == null) {
            throw new CustomException(Error.SERVICES_NOT_EXIST.getMessage(), Error.SERVICES_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        ServiceCompanyEntity checkDuplicateNameEntity = serviceCompanyRepository.findByServiceName(domain.getName());
        if (checkDuplicateNameEntity != null && !checkDuplicateNameEntity.getServiceName().equals(domain.getName())) {
            throw new CustomException(Error.DUPLICATE_SERVICE_NAME.getMessage(), Error.DUPLICATE_SERVICE_NAME.getCode(), HttpStatus.BAD_REQUEST);
        }

        serviceCompanyEntity.setServiceName(domain.getName());
        serviceCompanyEntity.setNote(domain.getNote());
        serviceCompanyEntity.setPrice(StringUtils.convertStringToLongOrNull(domain.getPrice()));
        serviceCompanyEntity.setBanner(StringUtils.isEmpty(domain.getBanner()) ? "https://giupviecnhahcm.com/wp-content/uploads/2017/03/nhan-vien-giup-viec-248x300.png" : domain.getBanner());
        serviceCompanyEntity.setUpdatedBy(UserUtils.getCurrentUserId());
        serviceCompanyEntity.setUpdatedDate(new Date());
        serviceCompanyEntity.setIntroduce(domain.getIntroduces());
        serviceCompanyEntity.setAdvantages(domain.getAdvantage());

        serviceCompanyRepository.save(serviceCompanyEntity);
    }

    @Override
    public void createService(ServiceDomain domain) {
        if (StringUtils.isEmpty(domain.getName()) || StringUtils.isEmpty(domain.getPrice())) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        ServiceCompanyEntity serviceCompanyEntity = new ServiceCompanyEntity();
        ServiceCompanyEntity checkDuplicateNameEntity = serviceCompanyRepository.findByServiceName(domain.getName());
        if (checkDuplicateNameEntity != null) {
            throw new CustomException(Error.DUPLICATE_SERVICE_NAME.getMessage(), Error.DUPLICATE_SERVICE_NAME.getCode(), HttpStatus.BAD_REQUEST);
        }

        serviceCompanyEntity.setServiceName(domain.getName());
        serviceCompanyEntity.setNote(domain.getNote());
        serviceCompanyEntity.setPrice(StringUtils.convertStringToLongOrNull(domain.getPrice()));
        serviceCompanyEntity.setAdvantages(domain.getAdvantage());
        serviceCompanyEntity.setIntroduce(domain.getIntroduces());
        serviceCompanyEntity.setStatus(0);
        serviceCompanyEntity.setBanner(StringUtils.isEmpty(domain.getBanner()) ? "https://giupviecnhahcm.com/wp-content/uploads/2017/03/nhan-vien-giup-viec-248x300.png" : domain.getBanner());
        serviceCompanyRepository.save(serviceCompanyEntity);
    }

    @Override
    public List<ServiceOverviewDetailDomain> getOverviewDetailOfService(String startDate, String endDate) {
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<ServiceOverviewDetailDomain> details = new ArrayList<>();
        List<ServiceCompanyEntity> serviceCompanyEntities = serviceCompanyRepository.findAll();
//        List<ServiceTaskEntity> serviceTaskEntities = serviceTaskRepository.findAll();

        for (ServiceCompanyEntity serviceCompanyEntity : serviceCompanyEntities) {
            ServiceOverviewDetailDomain domain = new ServiceOverviewDetailDomain();
            List<ItemOnDateDomain> itemList = new ArrayList<>();
            for (Date date : dateList) {
                ItemOnDateDomain item = new ItemOnDateDomain();
                item.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));

                Integer num = taskRepository.findByServiceId(serviceCompanyEntity.getId()).size();
                item.setNumber(StringUtils.convertObjectToString(num));
                itemList.add(item);
            }
            domain.setDetails(itemList);
            domain.setService(serviceCompanyEntity.getServiceName());
            details.add(domain);
        }
        return details;
    }

    @Override
    public OverviewServiceDomain getServiceOverviewDetail(String startDate, String endDate) {
        OverviewServiceDomain overviewTaskDomain = new OverviewServiceDomain();
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<ServiceCompanyEntity> serviceCompanyEntities = serviceCompanyRepository.findAll();
        overviewTaskDomain.setNumService(StringUtils.convertObjectToString(serviceCompanyEntities.size()));
        List<TaskEntity> serviceTaskEntities = taskRepository.findAll();
        Map.Entry<Long, Long> maxTaskPerService = serviceTaskEntities.stream().collect(Collectors.groupingBy(t -> t.getServiceId(), Collectors.counting())).entrySet()
                .stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).findFirst().get();
        Long maxServiceId = maxTaskPerService.getKey();
        ServiceCompanyEntity maxService = serviceCompanyRepository.findById(maxServiceId).orElse(null);
        ServiceDomain maxDomain = new ServiceDomain();
        maxDomain.setName(maxService.getServiceName());
        maxDomain.setBanner(maxService.getBanner());
        maxDomain.setNumTask(StringUtils.convertObjectToString(maxTaskPerService.getValue()));
        overviewTaskDomain.setMaxService(maxDomain);

        Map.Entry<Long, Long> minTaskPerService = serviceTaskEntities.stream().collect(Collectors.groupingBy(t -> t.getServiceId(), Collectors.counting())).entrySet()
                .stream().sorted(Comparator.comparing(Map.Entry::getValue)).findFirst().get();
        Long minServiceId = minTaskPerService.getKey();
        ServiceCompanyEntity minService = serviceCompanyRepository.findById(minServiceId).orElse(null);
        ServiceDomain minDomain = new ServiceDomain();
        minDomain.setName(minService.getServiceName());
        minDomain.setBanner(minService.getBanner());
        minDomain.setNumTask(StringUtils.convertObjectToString(minTaskPerService.getValue()));
        overviewTaskDomain.setMinService(minDomain);

        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<ServiceOverviewDetailDomain> details = new ArrayList<>();

        for (ServiceCompanyEntity serviceCompanyEntity : serviceCompanyEntities) {
            ServiceOverviewDetailDomain domain = new ServiceOverviewDetailDomain();
            List<ItemOnDateDomain> itemList = new ArrayList<>();
            for (Date date : dateList) {
                ItemOnDateDomain item = new ItemOnDateDomain();
                item.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
                Integer num = serviceTaskEntities.stream().filter(t -> {
                    String createDate = StringUtils.convertDateToStringFormatPattern(t.getCreatedDate(), DateTimeUtils.YYYYMMDD);
                    Date cd = DateTimeUtils.convertStringToDateOrNull(createDate, DateTimeUtils.YYYYMMDD);
                    return t.getServiceId() == serviceCompanyEntity.getId() && cd.getTime() == date.getTime();
                })
                        .collect(Collectors.toList()).size();
                item.setNumber(StringUtils.convertObjectToString(num));
                itemList.add(item);
            }
            domain.setDetails(itemList);
            domain.setService(serviceCompanyEntity.getServiceName());
            details.add(domain);
        }
        overviewTaskDomain.setDetails(details);
        return overviewTaskDomain;
    }

    @Override
    public List<ServiceOverviewDetailDomain> getOverviewDetailOfServiceForEmployee(String startDate, String endDate) {
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<ServiceOverviewDetailDomain> details = new ArrayList<>();
        List<ServiceCompanyEntity> serviceCompanyEntities = serviceCompanyRepository.findAll();
//        List<ServiceTaskEntity> serviceTaskEntities = serviceTaskRepository.findAllByEmployeeId(UserUtils.getCurrentUserId());

        for (ServiceCompanyEntity serviceCompanyEntity : serviceCompanyEntities) {
            ServiceOverviewDetailDomain domain = new ServiceOverviewDetailDomain();
            List<ItemOnDateDomain> itemList = new ArrayList<>();
            for (Date date : dateList) {
                ItemOnDateDomain item = new ItemOnDateDomain();
                item.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
                Integer num = taskRepository.findByServiceId(serviceCompanyEntity.getId()).size();
                item.setNumber(StringUtils.convertObjectToString(num));
                itemList.add(item);
            }
            domain.setDetails(itemList);
            domain.setService(serviceCompanyEntity.getServiceName());
            details.add(domain);
        }
        return details;
    }

    @Override
    public OverviewServiceDomain getServiceOverviewDetailForEmployee(String startDate, String endDate) {
        OverviewServiceDomain overviewTaskDomain = new OverviewServiceDomain();
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<ServiceCompanyEntity> serviceCompanyEntities = serviceCompanyRepository.findAll();
        overviewTaskDomain.setNumService(StringUtils.convertObjectToString(serviceCompanyEntities.size()));
        List<TaskEntity> serviceTaskEntities = taskRepository.findByEmployeeId(UserUtils.getCurrentUserId());
        Map.Entry<Long, Long> maxTaskPerService = serviceTaskEntities.stream().collect(Collectors.groupingBy(t -> t.getServiceId(), Collectors.counting())).entrySet()
                .stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).findFirst().get();
        Long maxServiceId = maxTaskPerService.getKey();
        ServiceCompanyEntity maxService = serviceCompanyRepository.findById(maxServiceId).orElse(null);
        ServiceDomain maxDomain = new ServiceDomain();
        maxDomain.setName(maxService.getServiceName());
        maxDomain.setBanner(maxService.getBanner());
        maxDomain.setNumTask(StringUtils.convertObjectToString(maxTaskPerService.getValue()));
        overviewTaskDomain.setMaxService(maxDomain);

        Map.Entry<Long, Long> minTaskPerService = serviceTaskEntities.stream().collect(Collectors.groupingBy(t -> t.getServiceId(), Collectors.counting())).entrySet()
                .stream().sorted(Comparator.comparing(Map.Entry::getValue)).findFirst().get();
        Long minServiceId = minTaskPerService.getKey();
        ServiceCompanyEntity minService = serviceCompanyRepository.findById(minServiceId).orElse(null);
        ServiceDomain minDomain = new ServiceDomain();
        minDomain.setName(minService.getServiceName());
        minDomain.setBanner(minService.getBanner());
        minDomain.setNumTask(StringUtils.convertObjectToString(minTaskPerService.getValue()));
        overviewTaskDomain.setMinService(minDomain);

        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<ServiceOverviewDetailDomain> details = new ArrayList<>();

        for (ServiceCompanyEntity serviceCompanyEntity : serviceCompanyEntities) {
            ServiceOverviewDetailDomain domain = new ServiceOverviewDetailDomain();
            List<ItemOnDateDomain> itemList = new ArrayList<>();
            for (Date date : dateList) {
                ItemOnDateDomain item = new ItemOnDateDomain();
                item.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
                Integer num = serviceTaskEntities.stream().filter(t -> {
                    String createDate = StringUtils.convertDateToStringFormatPattern(t.getCreatedDate(), DateTimeUtils.YYYYMMDD);
                    Date cd = DateTimeUtils.convertStringToDateOrNull(createDate, DateTimeUtils.YYYYMMDD);
                    return t.getServiceId() == serviceCompanyEntity.getId() && cd.getTime() == date.getTime();
                })
                        .collect(Collectors.toList()).size();
                item.setNumber(StringUtils.convertObjectToString(num));
                itemList.add(item);
            }
            domain.setDetails(itemList);
            domain.setService(serviceCompanyEntity.getServiceName());
            details.add(domain);
        }
        overviewTaskDomain.setDetails(details);
        return overviewTaskDomain;
    }

    @Override
    public void changeStatusService(ChangeStatusDomain domain) {
        Long serviceId = StringUtils.convertStringToLongOrNull(domain.getId());
        Integer status = ServiceStatus.getEmployeeStatusByValue(domain.getStatus()).getCode();
        ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.findById(serviceId).orElse(null);
        if (status == 1) {
            List<EmployeeServiceEntity> employeeServiceEntities = employeeServiceRepository.findByServiceId(serviceId);
            if (CollectionUtils.isEmpty(employeeServiceEntities)) {
                throw new CustomException("Kh??ng th??? cung c???p d???ch v??? khi ch??a c?? nh??n vi??n n??o c?? kinh nghi???m v??? vi???c l??m c???a d???ch v???", "S1234", HttpStatus.BAD_REQUEST);
            }
        }
        serviceCompanyEntity.setStatus(status);
        serviceCompanyRepository.save(serviceCompanyEntity);
    }
}
