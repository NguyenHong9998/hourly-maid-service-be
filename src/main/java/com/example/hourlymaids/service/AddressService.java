package com.example.hourlymaids.service;

import com.example.hourlymaids.domain.AddressDomain;

import java.util.List;

public interface AddressService {
    List<AddressDomain> getListAddressOfUser();

    AddressDomain getAddressDetail(String addressId);

    void updateAddress(String addressId, AddressDomain domain);

    void createAddress(AddressDomain domain);
}
