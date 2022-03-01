package com.example.hourlymaids.service;

import com.example.hourlymaids.entity.UserEntity;
import com.example.hourlymaids.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean checkExistClient(String email, String phone) {
        UserEntity clientEntity = userRepository.findByEmail(email);
        if(clientEntity != null){
            return true;
        }
        return false;
    }
}
