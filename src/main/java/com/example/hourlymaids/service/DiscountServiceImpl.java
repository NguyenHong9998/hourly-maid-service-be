package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.constant.*;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.entity.DiscountEntity;
import com.example.hourlymaids.entity.NotifyEntity;
import com.example.hourlymaids.entity.ServiceCompanyEntity;
import com.example.hourlymaids.entity.ServiceDiscountEntity;
import com.example.hourlymaids.repository.DiscountRepository;
import com.example.hourlymaids.repository.ServiceCompanyRepository;
import com.example.hourlymaids.repository.ServiceDiscountRepository;
import com.example.hourlymaids.repository.TaskRepository;
import com.example.hourlymaids.util.DateTimeUtils;
import com.example.hourlymaids.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private ServiceCompanyRepository serviceCompanyRepository;
    @Autowired
    private ServiceDiscountRepository serviceDiscountRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public ResponseDataAPI getListDiscount(GetListRequest request) {
        Integer offset = (request.getOffset() == null || request.getOffset() < 1) ? 0 : request.getOffset() - 1;
        Integer limit = (request.getLimit() == null || request.getLimit() < 1) ? 10 : request.getLimit();

        request.setLimit(limit);
        request.setOffset(offset);

        List<String> columnSort = Arrays.asList(ColumnSortDiscount.END_TIME.getName(), ColumnSortDiscount.START_TIME.getName(),
                ColumnSortDiscount.TITLE.getName());
        Pageable pageable = null;

        if (columnSort.contains(request.getColumnSort())) {
            if (ColumnSortDiscount.END_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortDiscount.END_TIME.getValue());
            } else if (ColumnSortDiscount.START_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortDiscount.START_TIME.getValue());
            } else if (ColumnSortDiscount.TITLE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortDiscount.TITLE.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortDiscount.CREATE_DATE.getValue()).descending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch());

        Page<DiscountEntity> entities = discountRepository.findAllDiscount(valueSearch, pageable);

        List<Object> result = entities.stream().map(discountEntity -> {
            if (discountEntity.getEndTime().getTime() <= new Date().getTime()) {
                discountEntity.setIsPublic(2);
                discountEntity = discountRepository.save(discountEntity);
            }
            DiscountDomain domain = new DiscountDomain();
            domain.setId(discountEntity.getId().toString());
            domain.setBanner(discountEntity.getBanner());
            domain.setNote(discountEntity.getNote());
            domain.setStartTime(StringUtils.convertDateToStringFormatPattern(discountEntity.getStartTime(), DateTimeUtils.YYYYMMDDhhmmss));
            domain.setEndTime(StringUtils.convertDateToStringFormatPattern(discountEntity.getEndTime(), DateTimeUtils.YYYYMMDDhhmmss));
            domain.setTitle(discountEntity.getTitle());
            domain.setBanner(discountEntity.getBanner());
            domain.setPublic(DiscountStatus.getDiscountStatusByCode(discountEntity.getIsPublic()).getValue());
            Integer numService = taskRepository.findListTaskByDiscountId(discountEntity.getId()).size();

            domain.setNumberService(numService.toString());
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
                    Sort.by(Sort.Order.desc(ColumnSortDiscount.CREATE_DATE.getValue())));
        }
        return pageable;
    }

    @Override
    public void createDiscount(DiscountDomain domain) {

        String title = domain.getTitle();
        if (StringUtils.isEmpty(title)) {
            throw new CustomException(Error.DISCOUNT_TITLE_EMPTY.getMessage(), Error.DISCOUNT_TITLE_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }

        Date startDate = DateTimeUtils.convertStringToDateOrNull(domain.getStartTime(), DateTimeUtils.YYYYMMDDhhmmss);
        Date endDate = DateTimeUtils.convertStringToDateOrNull(domain.getEndTime(), DateTimeUtils.YYYYMMDDhhmmss);
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new CustomException(Error.START_TIME_OR_DATE_TIME_INVALID.getMessage(), Error.START_TIME_OR_DATE_TIME_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        DiscountEntity discountEntity = new DiscountEntity();
        discountEntity.setEndTime(endDate);
        discountEntity.setStartTime(startDate);
        discountEntity.setNote(domain.getNote());
        discountEntity.setBanner(StringUtils.isEmpty(domain.getBanner()) ? "https://cdn-www.vinid.net/2020/09/164e51ba-2-top-banner-1920x1080-1.jpg" : domain.getBanner());
        discountEntity.setTitle(domain.getTitle());
        discountEntity.setIsPublic(DiscountStatus.INACTIVE.getCode());
        discountEntity = discountRepository.save(discountEntity);

        List<ServiceDiscountEntity> serviceDiscountEntities = new ArrayList<>();
        for (ServiceParamDomain service : domain.getServiceList()) {
            String serviceName = service.getName();
            if (StringUtils.isEmpty(serviceName)) {
                throw new CustomException(Error.SERVICES_NOT_EXIST.getMessage(), Error.SERVICES_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
            }
            ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.findByServiceName(serviceName);
            if (serviceCompanyEntity == null) {
                throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }
            ServiceDiscountEntity serviceDiscountEntity = new ServiceDiscountEntity();
            serviceDiscountEntity.setServiceId(serviceCompanyEntity.getId());
            serviceDiscountEntity.setDiscountId(discountEntity.getId());
            Long percentageService = StringUtils.convertStringToLongOrNull(service.getPercentage());
            serviceDiscountEntity.setSalePercentage(percentageService);
            serviceDiscountEntities.add(serviceDiscountEntity);
        }
        serviceDiscountRepository.saveAll(serviceDiscountEntities);
    }

    @Transactional
    @Override
    public void updateDiscount(String discountId, DiscountDomain domain) {
        Long id = StringUtils.convertStringToLongOrNull(discountId);
        if (id == null) {
            throw new CustomException(Error.DISCOUNT_NOT_EXIST.getMessage(), Error.DISCOUNT_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }

        DiscountEntity discountEntity = discountRepository.findById(id).orElse(null);
        if (discountEntity == null) {
            throw new CustomException(Error.DISCOUNT_NOT_EXIST.getMessage(), Error.DISCOUNT_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }

        String title = domain.getTitle();
        if (StringUtils.isEmpty(title)) {
            throw new CustomException(Error.TITLE_EMPTY.getMessage(), Error.TITLE_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }

        Date startDate = DateTimeUtils.convertStringToDateOrNull(domain.getStartTime(), DateTimeUtils.YYYYMMDDhhmmss);
        Date endDate = DateTimeUtils.convertStringToDateOrNull(domain.getEndTime(), DateTimeUtils.YYYYMMDDhhmmss);
        if (startDate == null || endDate == null || startDate.after(endDate)) {
            throw new CustomException(Error.START_TIME_OR_DATE_TIME_INVALID.getMessage(), Error.START_TIME_OR_DATE_TIME_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        discountEntity.setEndTime(endDate);
        discountEntity.setStartTime(startDate);
        discountEntity.setNote(domain.getNote());
        discountEntity.setBanner(domain.getBanner());
        discountEntity.setTitle(domain.getTitle());
        discountRepository.save(discountEntity);

        if (domain.getServiceList().isEmpty()) {
            serviceDiscountRepository.deleteByDiscountId(id);
        } else {
            serviceDiscountRepository.deleteByDiscountId(id);
            List<ServiceDiscountEntity> serviceDiscountEntities = new ArrayList<>();
            for (ServiceParamDomain service : domain.getServiceList()) {
                if (StringUtils.isEmpty(service.getName())) {
                    throw new CustomException(Error.SERVICES_NOT_EXIST.getMessage(), Error.SERVICES_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
                }
                ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.findByServiceName(service.getName());
                if (serviceCompanyEntity == null) {
                    throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                }
                ServiceDiscountEntity serviceDiscountEntity = new ServiceDiscountEntity();
                serviceDiscountEntity.setServiceId(serviceCompanyEntity.getId());
                serviceDiscountEntity.setDiscountId(id);
                Long percentageService = StringUtils.convertStringToLongOrNull(service.getPercentage());
                serviceDiscountEntity.setSalePercentage(percentageService);
                serviceDiscountEntities.add(serviceDiscountEntity);
            }
            serviceDiscountRepository.saveAll(serviceDiscountEntities);

        }
    }

    @Override
    public DiscountDomain getDiscountDetail(String discountId) {
        Long id = StringUtils.convertStringToLongOrNull(discountId);
        if (id == null) {
            throw new CustomException(Error.DISCOUNT_NOT_EXIST.getMessage(), Error.DISCOUNT_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        DiscountEntity discountEntity = discountRepository.findById(id).orElse(null);
        if (discountEntity == null) {
            throw new CustomException(Error.DISCOUNT_NOT_EXIST.getMessage(), Error.DISCOUNT_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        DiscountDomain domain = new DiscountDomain();
        domain.setBanner(discountEntity.getBanner());
        domain.setTitle(discountEntity.getTitle());
        domain.setEndTime(StringUtils.convertDateToStringFormatPattern(discountEntity.getEndTime(), DateTimeUtils.YYYYMMDDhhmmss));
        domain.setStartTime(StringUtils.convertDateToStringFormatPattern(discountEntity.getStartTime(), DateTimeUtils.YYYYMMDDhhmmss));
        domain.setNote(discountEntity.getNote());
//        domain.setPublic(discountEntity.getIsPublic() == 1 ? true : false);
        List<ServiceParamDomain> discountEntities = serviceDiscountRepository.findByDiscountId(id).stream().map(s -> {
            ServiceParamDomain serviceParamDomain = new ServiceParamDomain();
            ServiceCompanyEntity serviceCompanyEntity = (ServiceCompanyEntity) s[0];
            Long percentage = (Long) s[1];
            serviceParamDomain.setName(serviceCompanyEntity.getServiceName());
            serviceParamDomain.setId(serviceCompanyEntity.getId().toString());
            serviceParamDomain.setPercentage(percentage.toString());
            serviceParamDomain.setBanner(serviceCompanyEntity.getBanner());
            return serviceParamDomain;
        }).collect(Collectors.toList());
        domain.setServiceList(discountEntities);
        return domain;
    }

    @Override
    public void changeStatusDiscount(ChangeNotifyStatusDomain domain) {
        Long id = StringUtils.convertObjectToLongOrNull(domain.getId());

        DiscountEntity discountEntity = discountRepository.findById(id).orElse(null);
        if (discountEntity == null) {
            throw new CustomException(Error.NOTIFY_NOT_FOUND.getMessage(), Error.NOTIFY_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        discountEntity.setIsPublic(DiscountStatus.ACTIVE.getCode());
        discountRepository.save(discountEntity);
    }

    @Transactional
    @Override
    public void deleteDomain(DeleteDomain deleteDomain) {
        List<Long> ids = deleteDomain.getIds().stream().map(t -> StringUtils.convertObjectToLongOrNull(t)).collect(Collectors.toList());
        discountRepository.deleteAllById(ids);
    }
}
