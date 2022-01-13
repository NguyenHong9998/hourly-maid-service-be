package com.example.hourlymaids.service;

import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.EmployeeServiceDomain;
import com.example.hourlymaids.entity.EmployeeServiceEntity;
import com.example.hourlymaids.entity.ServiceCompanyEntity;
import com.example.hourlymaids.entity.UserEntity;
import com.example.hourlymaids.repository.EmployeeServiceRepository;
import com.example.hourlymaids.repository.ServiceCompanyRepository;
import com.example.hourlymaids.repository.UserRepository;
import com.example.hourlymaids.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<EmployeeServiceDomain> getListUserOfServiceId(String serviceId) {
        Long service = StringUtils.convertStringToLongOrNull(serviceId);
        if (service == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        ServiceCompanyEntity serviceCompanyEntity = serviceCompanyRepository.getById(service);
        if (serviceCompanyEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        List<EmployeeServiceEntity> entities = employeeServiceRepository.findByServiceId(service);

        List<EmployeeServiceDomain> domains = entities.stream().map(entity -> {
            EmployeeServiceDomain domain = new EmployeeServiceDomain();
            UserEntity user = userRepository.findById(entity.getUserId()).orElse(null);
            domain.setServiceId(serviceCompanyEntity.getId().toString());
            domain.setServiceName(serviceCompanyEntity.getServiceName());
            domain.setUserId(user.getId().toString());
            domain.setUserName(user.getFullName());
            domain.setUserAvatar(user.getAvatar());
            domain.setLevel(entity.getLevel().toString());
            return domain;
        }).collect(Collectors.toList());
        return domains;
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
}
