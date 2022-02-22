package com.example.hourlymaids.service;

import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.domain.*;

import java.util.List;

public interface UserService {
    LoginResponse checkLogin(LoginUser loginUser) throws Exception;

    LoginResponse checkRegister(UserInformDomain registerUserDomain) throws Exception;

    void resetPassword(ChangePasswordDomain resetPasswordDomain);

    UserInformDomain getProfile();

    void sendMailForgotPassword(ForgotPasswordDomain forgotPasswordDomain);

    void sendMailVerifyEmail(VerifyEmailDomain verifyEmailDomain);

    void sendSMSVerifyPhone(VerifyPhoneDomain verifyPhoneDomain);

    void verifyEmail(VerifyDomain verifyDomain);

    void verifyPhone(VerifyDomain verifyDomain);

    void resetPassword(ResetPasswordDomain resetPasswordDomain);

    CommonInformDomain getUserCommonInform();

    void updateUserCommonInform(CommonInformDomain domain);

    UserPersonalInformDomain getUserPersonalInform();

    void updateUserPersonalInform(UserPersonalInformDomain domain);

    void changePassword(ChangePasswordDomain domain);

    ResponseDataAPI getListEmployee(GetListRequest request);

    void createUser(EmployeeListDomain domain);

    void changeStatusEmployee(ChangeStatusEmployeeDomain domain);

    CommonInformDomain getUserCommonInforById(String id);

    UserPersonalInformDomain getUserPersonalInformById(String id);

    void updateUserCommonInformById(CommonInformDomain domain, String id);

    void updateUserPersonalInform(UserPersonalInformDomain domain, String id);

    List<UserInformDomain> getOveriewDetailOfFeedbackuser(String startDate, String endDate);

    UserOverviewDomain getOveriewOfFeedbackUser(String startDate, String endDate);
}
