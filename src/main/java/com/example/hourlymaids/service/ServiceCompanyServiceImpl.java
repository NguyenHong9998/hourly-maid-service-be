package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.constant.ColumnSortService;
import com.example.hourlymaids.constant.ConstantDefine;
import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.EmployeeServiceDomain;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.domain.ServiceDomain;
import com.example.hourlymaids.entity.ServiceCompanyEntity;
import com.example.hourlymaids.repository.ServiceCompanyRepository;
import com.example.hourlymaids.util.StringUtils;
import com.example.hourlymaids.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceCompanyServiceImpl implements ServiceCompanyService {
    @Autowired
    private ServiceCompanyRepository serviceCompanyRepository;

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
            }else if(ColumnSortService.NOTE.getName().equals(request.getColumnSort())){
                request.setColumnSort(ColumnSortService.NOTE.getValue());
            }else if(ColumnSortService.CREATED_DATE.getName().equals(request.getColumnSort())){
                request.setColumnSort(ColumnSortService.CREATED_DATE.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortService.CREATED_DATE.getValue()).descending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch());

        Page<ServiceCompanyEntity> entities = serviceCompanyRepository.findAllService(valueSearch, pageable);

        List<Object> result = entities.stream().map(service -> {
            ServiceDomain serviceDomain = new ServiceDomain();
            serviceDomain.setId(service.getId().toString());
            serviceDomain.setName(service.getServiceName());
            serviceDomain.setPrice(StringUtils.convertObjectToString(service.getPrice()));
            serviceDomain.setNote(service.getNote());
            serviceDomain.setBanner(service.getBanner());
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

        serviceCompanyEntity.setBanner(StringUtils.isEmpty(domain.getBanner()) ? "https://giupviecnhahcm.com/wp-content/uploads/2017/03/nhan-vien-giup-viec-248x300.png" : domain.getBanner());

        serviceCompanyRepository.save(serviceCompanyEntity);
    }

}
