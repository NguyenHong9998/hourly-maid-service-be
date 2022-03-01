package com.example.hourlymaids.controller;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.service.EmployeeServiceService;
import com.example.hourlymaids.service.UserService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmployeeServiceService employeeServiceService;

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

    @PutMapping("/client/common-inform")
    public ResponseEntity<Object> updateClientCommonInform(@RequestBody ClientCommonInform domain) {
        userService.updateClientCommonInform(domain);
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

    @GetMapping("/list")
    public ResponseEntity<Object> getListEmployee(@RequestParam(value = "offset") @ApiParam(value = "offset", example = "0") Integer offset,
                                                  @RequestParam(value = "limit") @ApiParam(value = "limit", example = "10") Integer limit,
                                                  @RequestParam(value = "status") @ApiParam(value = "status", example = "1") String status,
                                                  @RequestParam(value = "value_search") @ApiParam(value = "valueSearch", example = "") String valueSearch,
                                                  @RequestParam(value = "column_sort") @ApiParam(value = "columnSort", example = "TITLE") String columnSort,
                                                  @RequestParam(value = "type_sort") @ApiParam(value = "typeSort", example = "ASC") String typeSort) {
        Integer of = (offset == null || offset <= 1) ? 0 : offset - 1;
        Integer lim = (limit == null || limit < 1) ? 10 : limit;
        GetListRequest getListRequest = new GetListRequest();
        getListRequest.setLimit(lim);
        getListRequest.setOffset(of);
        getListRequest.setColumnSort(columnSort);
        getListRequest.setTypeSort(typeSort);
        getListRequest.setValueSearch(valueSearch);
        getListRequest.setStatus(status);
        ResponseDataAPI responseDataAPI = userService.getListEmployee(getListRequest);
        return ResponseEntity.ok(responseDataAPI);
    }

    @PostMapping("")
    public ResponseEntity<Object> createEmployee(@RequestBody EmployeeListDomain employeeListDomain) {
        userService.createUser(employeeListDomain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PutMapping("/change-status")
    public ResponseEntity<Object> changeStatusEmployee(@RequestBody ChangeStatusDomain domain) {
        userService.changeStatusEmployee(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/common-inform/{user_id}")
    public ResponseEntity<Object> getUserCommonInform(@PathVariable("user_id") String userId) {

        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getUserCommonInforById(userId)).build());
    }

    @GetMapping("/personal-inform/{user_id}")
    public ResponseEntity<Object> getUserPersonalCommonInform(@PathVariable("user_id") String userId) {

        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getUserPersonalInformById(userId)).build());
    }


    @PutMapping("/common-inform/{user_id}")
    public ResponseEntity<Object> updateUserCommonInformById(@PathVariable("user_id") String id, @RequestBody CommonInformDomain domain) {
        userService.updateUserCommonInformById(domain, id);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PutMapping("/personal-inform/{user_id}")
    public ResponseEntity<Object> updateUserPersonalInformById(@PathVariable("user_id") String id, @RequestBody UserPersonalInformDomain domain) {
        userService.updateUserPersonalInform(domain, id);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/experience/{user_id}")
    public ResponseEntity<Object> getListExperience(@PathVariable("user_id") String userId) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(employeeServiceService.getServiceListOfEmployee(userId)).build());
    }

    @GetMapping("/overview")
    private ResponseEntity<Object> getOveriewOfFeedbackUser(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getOveriewOfFeedbackUser(startDate, endDate)).build());
    }

    @GetMapping("/overview/detail")
    private ResponseEntity<Object> getOveriewDetailOfFeedbackuser(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getOveriewDetailOfFeedbackuser(startDate, endDate)).build());
    }

    @GetMapping("/overview/employee")
    private ResponseEntity<Object> getOveriewOfFeedbackUserForEmployee(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getOverviewOfEmployee(startDate, endDate)).build());
    }

    @GetMapping("/overview/employee/detail")
    private ResponseEntity<Object> getOveriewDetailOfFeedbackuserForEmployee(@RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getOverViewDetailOfEmployee(startDate, endDate)).build());
    }

    @GetMapping("/client/inform")
    private ResponseEntity<Object> getClientInform() {
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userService.getClientInform()).build());
    }

    @PostMapping("/employee/experience")
    private ResponseEntity<Object> updateemployeeEmperience(@RequestBody UpdateEmployeeExperienceDomain domain) {
        employeeServiceService.updateListServiceOfEmployee(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }
}
