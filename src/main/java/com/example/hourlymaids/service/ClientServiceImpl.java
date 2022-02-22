package com.example.hourlymaids.service;

import com.example.hourlymaids.entity.ClientEntity;
import com.example.hourlymaids.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public boolean checkExistClient(String email, String phone) {
        ClientEntity clientEntity = clientRepository.findByEmail(email);
        if(clientEntity != null){
            return true;
        }
        return false;
    }
}
