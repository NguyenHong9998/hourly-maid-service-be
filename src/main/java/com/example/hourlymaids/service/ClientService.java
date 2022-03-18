package com.example.hourlymaids.service;

import com.example.hourlymaids.domain.UserInformDomain;

public interface ClientService {
    UserInformDomain checkExistClient(String email, String phone);
}
