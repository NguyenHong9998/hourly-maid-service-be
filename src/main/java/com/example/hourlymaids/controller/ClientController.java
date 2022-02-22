package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping("")
    public ResponseEntity<ResponseDataAPI> getClientInform(@RequestParam(value = "client_id", required = false) String clientId) {
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/check")
    public ResponseEntity<ResponseDataAPI> checkExistUser(@RequestParam(value = "phone") String phone, @RequestParam(value = "email") String email) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(clientService.checkExistClient(email, phone)).build());
    }

}
