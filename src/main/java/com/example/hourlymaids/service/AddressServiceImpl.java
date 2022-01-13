package com.example.hourlymaids.service;

import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.AddressDomain;
import com.example.hourlymaids.entity.AddressEntity;
import com.example.hourlymaids.repository.AddressRepository;
import com.example.hourlymaids.util.StringUtils;
import com.example.hourlymaids.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public List<AddressDomain> getListAddressOfUser() {
        List<AddressEntity> entities = addressRepository.findByUserId(UserUtils.getCurrentUserId());
        List<AddressDomain> result = entities.stream().map(addressEntity -> {
            AddressDomain domain = new AddressDomain();
            domain.setAddress(addressEntity.getAddress());
            domain.setDescription(addressEntity.getDescription());
            domain.setType(StringUtils.convertObjectToString(addressEntity.getType()));
            return domain;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public AddressDomain getAddressDetail(String addressId) {
        Long id = StringUtils.convertStringToLongOrNull(addressId);
        if (id == null) {
            throw new CustomException(Error.ADDRESS_NOT_FOUND.getMessage(), Error.ADDRESS_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        AddressEntity addressEntity = addressRepository.findById(id).orElse(null);
        if (addressEntity == null) {
            throw new CustomException(Error.ADDRESS_NOT_FOUND.getMessage(), Error.ADDRESS_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        AddressDomain domain = new AddressDomain();
        domain.setAddress(addressEntity.getAddress());
        domain.setDescription(addressEntity.getDescription());
        domain.setType(StringUtils.convertObjectToString(addressEntity.getType()));
        return domain;
    }

    @Override
    public void updateAddress(String addressId, AddressDomain domain) {
        Long id = StringUtils.convertStringToLongOrNull(addressId);
        if (id == null) {
            throw new CustomException(Error.ADDRESS_NOT_FOUND.getMessage(), Error.ADDRESS_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        AddressEntity addressEntity = addressRepository.findById(id).orElse(null);
        if (addressEntity == null) {
            throw new CustomException(Error.ADDRESS_NOT_FOUND.getMessage(), Error.ADDRESS_NOT_FOUND.getCode(), HttpStatus.BAD_REQUEST);
        }
        addressEntity.setAddress(domain.getAddress());
        addressEntity.setType(StringUtils.convertStringToIntegerOrNull(domain.getType()));
        addressEntity.setDescription(domain.getDescription());
        addressEntity.setUpdatedBy(UserUtils.getCurrentUserId());
        addressEntity.setUpdatedDate(new Date());
        addressRepository.save(addressEntity);
    }

    @Override
    public void createAddress(AddressDomain domain) {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddress(domain.getAddress());
        addressEntity.setType(StringUtils.convertStringToIntegerOrNull(domain.getType()));
        addressEntity.setDescription(domain.getDescription());
        addressEntity.setUserId(UserUtils.getCurrentUserId());
        addressRepository.save(addressEntity);
    }
}
