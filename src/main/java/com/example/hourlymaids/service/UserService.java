package com.example.hourlymaids.service;

import com.example.hourlymaids.domain.LoginResponse;
import com.example.hourlymaids.domain.LoginUser;
import com.example.hourlymaids.domain.UserInformDomain;
import com.example.hourlymaids.domain.ResetPasswordDomain;

public interface UserService {
    LoginResponse checkLogin(LoginUser loginUser) throws Exception;

    LoginResponse checkRegister(UserInformDomain registerUserDomain) throws Exception;

    void resetPassword(ResetPasswordDomain resetPasswordDomain);

    UserInformDomain getProfile();
}
