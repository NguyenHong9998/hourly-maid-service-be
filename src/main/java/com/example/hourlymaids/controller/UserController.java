package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/profile")
    public ResponseEntity<Object> getUserProfile() {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getProfile()).build());
    }

    @PostMapping(value = "/send-verify-email")
    public ResponseEntity<ResponseDataAPI> sendMailVerifyEmail(@RequestBody VerifyEmailDomain verifyEmailDomain) {
        userService.sendMailVerifyEmail(verifyEmailDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(null).build());
    }

    @PostMapping(value = "/verify-phone-sms")
    public ResponseEntity<ResponseDataAPI> sendMailVerifyPhone(@RequestBody VerifyPhoneDomain verifyPhoneDomain) {
        userService.sendSMSVerifyPhone(verifyPhoneDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(null).build());
    }

    @PostMapping(value = "/forgot-pass-email")
    public ResponseEntity<ResponseDataAPI> sendMailForgotPass(@RequestBody ForgotPasswordDomain forgotPasswordDomain) {
        userService.sendMailForgotPassword(forgotPasswordDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(null).build());
    }


    @PostMapping(value = "/verify-email")
    public ResponseEntity<ResponseDataAPI> verifyEmail(@RequestBody VerifyDomain verifyDomain) {
        userService.verifyEmail(verifyDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(null).build());
    }

    @PostMapping(value = "/verify-phone")
    public ResponseEntity<ResponseDataAPI> verifyPhone(@RequestBody VerifyDomain verifyDomain) {
        userService.verifyPhone(verifyDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(null).build());
    }

    @PostMapping(value = "/reset-pass")
    public ResponseEntity<ResponseDataAPI> resetPassword(@RequestBody ResetPasswordDomain resetPasswordDomain) {
        userService.resetPassword(resetPasswordDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(null).build());
    }

    @GetMapping("/common-inform")
    public ResponseEntity<Object> getUserCommonInform() {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getUserCommonInform()).build());
    }

    @PutMapping("/common-inform")
    public ResponseEntity<Object> updateUserCommonInform(@RequestBody CommonInformDomain domain) {
        userService.updateUserCommonInform(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/personal-inform")
    public ResponseEntity<Object> getUserPersonalInform() {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getUserPersonalInform()).build());
    }

    @PutMapping("/personal-inform")
    public ResponseEntity<Object> updateUserPersonalInform(@RequestBody UserPersonalInformDomain domain) {
        userService.updateUserPersonalInform(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/change-pass")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDomain domain) {
        userService.changePassword(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }


}
