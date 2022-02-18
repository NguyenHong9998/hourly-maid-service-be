package com.example.hourlymaids.service;

import com.example.hourlymaids.constant.*;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.EmployeeServiceDomain;
import com.example.hourlymaids.domain.GetListDiscountOfService;
import com.example.hourlymaids.domain.GetListRequest;
import com.example.hourlymaids.entity.DiscountEntity;
import com.example.hourlymaids.entity.EmployeeServiceEntity;
import com.example.hourlymaids.entity.ServiceCompanyEntity;
import com.example.hourlymaids.entity.UserEntity;
import com.example.hourlymaids.repository.EmployeeServiceRepository;
import com.example.hourlymaids.repository.ServiceCompanyRepository;
import com.example.hourlymaids.repository.ServiceDiscountRepository;
import com.example.hourlymaids.repository.UserRepository;
import com.example.hourlymaids.util.DateTimeUtils;
import com.example.hourlymaids.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceServiceImpl implements EmployeeServiceService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeServiceRepository employeeServiceRepository;
    @Autowired
    private ServiceCompanyRepository serviceCompanyRepository;
    @Autowired
    private ServiceDiscountRepository serviceDiscountRepository;

    @Override
    public List<EmployeeServiceDomain> getServiceListOfEmployee(String userId) {
        Long user = StringUtils.convertStringToLongOrNull(userId);
        if (user == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = userRepository.getById(user);
        if (userEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        List<EmployeeServiceEntity> entities = employeeServiceRepository.findByUserId(user);

        List<EmployeeServiceDomain> domains = entities.stream().map(entity -> {
            EmployeeServiceDomain domain = new EmployeeServiceDomain();
            ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.findById(entity.getServiceId()).orElse(null);
            domain.setServiceId(serviceCompanyEntity.getId().toString());
            domain.setServiceName(serviceCompanyEntity.getServiceName());
            domain.setUserId(userId);
            domain.setUserName(userEntity.getFullName());
            domain.setUserAvatar(userEntity.getAvatar());
            domain.setLevel(entity.getLevel().toString());
            return domain;
        }).collect(Collectors.toList());
        return domains;
    }


    @Override
    public List<EmployeeServiceDomain> getListUserOfServiceId(String serviceId, String typeSort, String columnSort) {
        Long service = StringUtils.convertStringToLongOrNull(serviceId);
        if (service == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.getById(service);
        if (serviceCompanyEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        GetListRequest request = new GetListRequest();
        request.setOffset(1);
        request.setLimit(1000);

        List<String> columnSorts = Arrays.asList(ColumnSortEmployeeService.NAME.getName(), ColumnSortEmployeeService.LEVEL.getName());
        Pageable pageable = null;

        if (columnSorts.contains(request.getColumnSort())) {
            if (ColumnSortEmployeeService.NAME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortEmployeeService.NAME.getValue());
            } else if (ColumnSortEmployeeService.LEVEL.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortEmployeeService.LEVEL.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortEmployeeService.LEVEL.getValue()).descending());
        }

        Page<Object[]> entities = employeeServiceRepository.findByServiceId(service, pageable);

        List<EmployeeServiceDomain> domains = entities.stream().map(objects -> {
            EmployeeServiceDomain domain = new EmployeeServiceDomain();
            UserEntity user = (UserEntity) objects[0];
            domain.setServiceId(serviceCompanyEntity.getId().toString());
            domain.setServiceName(serviceCompanyEntity.getServiceName());
            domain.setUserId(user.getId().toString());
            domain.setUserName(user.getFullName());
            domain.setUserAvatar(user.getAvatar());
            domain.setLevel(StringUtils.convertObjectToString(objects[1]));
            return domain;
        }).collect(Collectors.toList());
        return domains;
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
                    Sort.by(Sort.Order.desc(ColumnSortEmployeeService.LEVEL.getValue())));
        }
        return pageable;
    }


    @Override
    public void updateListServiceOfEmployee(String employeeId, List<EmployeeServiceDomain> domains) {
        Long user = StringUtils.convertStringToLongOrNull(employeeId);
        if (user == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = userRepository.getById(user);
        if (userEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        employeeServiceRepository.deleteByUserId(user);
        List<EmployeeServiceEntity> entities = new ArrayList<>();

        for (EmployeeServiceDomain domain : domains) {
            Long serviceId = StringUtils.convertStringToLongOrNull(domain.getServiceId());
            if (serviceId == null) {
                throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }
            ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.getById(serviceId);
            if (serviceCompanyEntity == null) {
                throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }
            EmployeeServiceEntity entity = new EmployeeServiceEntity();
            entity.setServiceId(serviceCompanyEntity.getId());
            entity.setLevel(StringUtils.convertStringToIntegerOrNull(domain.getLevel()));
            entity.setUserId(user);
            entities.add(entity);
        }

        employeeServiceRepository.saveAll(entities);
    }

    @Override
    public List<GetListDiscountOfService> getListDiscountOfService(String serviceId, String typeSort, String columnSort) {
        Long service = StringUtils.convertStringToLongOrNull(serviceId);
        if (service == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.getById(service);
        if (serviceCompanyEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        GetListRequest request = new GetListRequest();
        request.setOffset(0);
        request.setLimit(1000);

        List<String> columnSorts = Arrays.asList(ColumnSortDiscountService.TITLE.getName(), ColumnSortDiscountService.START_TIME.getName(),
                ColumnSortDiscountService.END_TIME.getName(), ColumnSortDiscountService.PERCENT.getName());
        Pageable pageable = null;

        if (columnSorts.contains(request.getColumnSort())) {
            if (ColumnSortDiscountService.TITLE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortDiscountService.TITLE.getValue());
            } else if (ColumnSortDiscountService.START_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortDiscountService.START_TIME.getValue());
            } else if (ColumnSortDiscountService.END_TIME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortDiscountService.END_TIME.getValue());
            } else if (ColumnSortDiscountService.PERCENT.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortDiscountService.START_TIME.getValue());
            }
            pageable = getPageableGetListDiscount(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortDiscountService.END_TIME.getValue()).descending());
        }

        Page<Object[]> entities = serviceDiscountRepository.getListDiscountOfService(service, pageable);

        List<GetListDiscountOfService> domains = entities.stream().map(objects -> {
            GetListDiscountOfService domain = new GetListDiscountOfService();
            DiscountEntity discountEntity = (DiscountEntity) objects[0];
            domain.setStartDate(StringUtils.convertDateToStringFormatPattern(discountEntity.getStartTime(), DateTimeUtils.YYYYMMDDhhmmss));
            domain.setEndDate(StringUtils.convertDateToStringFormatPattern(discountEntity.getEndTime(), DateTimeUtils.YYYYMMDDhhmmss));
            domain.setPercent(StringUtils.convertObjectToString(objects[1]));
            domain.setBanner(discountEntity.getBanner());
            domain.setStatus(DiscountStatus.getDiscountStatusByCode(discountEntity.getIsPublic()).getValue());
            return domain;
        }).collect(Collectors.toList());
        return domains;
    }

    private Pageable getPageableGetListDiscount(GetListRequest request, Pageable pageable) {
        if (ConstantDefine.SORT_ASC.equals(request.getTypeSort())) {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.asc(request.getColumnSort())));
        } else if (ConstantDefine.SORT_DESC.equals(request.getTypeSort())) {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.desc(request.getColumnSort())));
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.desc(ColumnSortEmployeeService.LEVEL.getValue())));
        }
        return pageable;
    }
}
