package com.example.hourlymaids.service;

import com.example.hourlymaids.domain.*;

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
}
