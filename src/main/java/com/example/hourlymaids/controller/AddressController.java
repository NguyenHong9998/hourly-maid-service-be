package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.AddressDomain;
import com.example.hourlymaids.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping("")
    public ResponseEntity<ResponseDataAPI> getUserAddress() {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(addressService.getListAddressOfUser()).build());
    }

    @GetMapping("/{address_id}")
    public ResponseEntity<ResponseDataAPI> getDiscountDetail(@PathVariable("address_id") String addressId) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(addressService.getAddressDetail(addressId)).build());
    }

    @PutMapping("/{address_id}")
    public ResponseEntity<ResponseDataAPI> editDiscountDetail(@PathVariable("address_id") String addressId, @RequestBody AddressDomain domain) {
        addressService.updateAddress(addressId, domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseDataAPI> createDiscount(@RequestBody AddressDomain domain) {
        addressService.createAddress(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

}
