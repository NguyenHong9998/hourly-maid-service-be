package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.ForgotPasswordDomain;
import com.example.hourlymaids.domain.LoginUser;
import com.example.hourlymaids.domain.UserInformDomain;
import com.example.hourlymaids.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResponseEntity<ResponseDataAPI> login(@RequestBody LoginUser userLoginDomain) throws Exception {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.checkLogin(userLoginDomain)).build());
    }

    @PostMapping(value = "/register")
    public ResponseEntity<ResponseDataAPI> register(@RequestBody UserInformDomain registerUserDomain) throws Exception {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.checkRegister(registerUserDomain)).build());
    }

    @PostMapping(value = "/forgot-pass")
    public ResponseEntity<ResponseDataAPI> resetPass(@RequestBody ForgotPasswordDomain forgotPasswordDomain) {
        userService.sendMailForgotPassword(forgotPasswordDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(null).build());
    }

}
