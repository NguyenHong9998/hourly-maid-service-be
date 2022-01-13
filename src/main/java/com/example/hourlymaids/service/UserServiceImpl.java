package com.example.hourlymaids.service;

import com.example.hourlymaids.config.CustomAuthenticationProvider;
import com.example.hourlymaids.config.TokenProvider;
import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.entity.*;
import com.example.hourlymaids.repository.AccountRepository;
import com.example.hourlymaids.repository.RoleRepository;
import com.example.hourlymaids.repository.UserRepository;
import com.example.hourlymaids.repository.UserVerifyRepository;
import com.example.hourlymaids.util.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CustomAuthenticationProvider authenticationProvider;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserVerifyRepository verifyRepository;
    @Autowired
    private SendMailUtils sendMailUtils;
    @Autowired
    private SendSmsUtils sendSmsUtils;

    @Override
    public LoginResponse checkLogin(LoginUser loginUser) throws Exception {
        LoginResponse response = new LoginResponse();
        if (StringUtils.isEmpty(loginUser.getEmail()) || StringUtils.isEmpty(loginUser.getPassword())) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        AccountEntity accountEntity = accountRepository.findByEmail(loginUser.getEmail());
        if (accountEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = userRepository.findByAccountId(accountEntity.getId());

        if (userEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDomain domain = (UserDomain) authentication.getPrincipal();
        response.setUserId(domain.getUserId());
        response.setEmail(domain.getEmail());
        response.setPassword(domain.getPassword());
        response.setRoles(domain.getRoles());
        response.setFullName(domain.getFullName());
        response.setAccessToken(tokenProvider.generateAccessToken(authentication));
        response.setRefreshToken(tokenProvider.generateRefreshToken(authentication));
        return response;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }

    @Override
    @Transactional
    public LoginResponse checkRegister(UserInformDomain registerUserDomain) throws Exception {
        if (StringUtils.isEmpty(registerUserDomain.getEmail()) || StringUtils.isEmpty(registerUserDomain.getPassword())) {
            throw new CustomException(Error.INVALID_EMAIL_OR_PASSWORD.getMessage(), Error.INVALID_EMAIL_OR_PASSWORD.getCode(), HttpStatus.BAD_REQUEST);
        }
        AccountEntity accountEntity = accountRepository.findByEmail(registerUserDomain.getEmail());
        if (accountEntity != null) {
            throw new CustomException(Error.EMAIL_EXIST.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }

        if (!registerUserDomain.isValidUser(registerUserDomain)) {
            throw new CustomException(Error.INVALID_PROFILE.getMessage(), Error.INVALID_PROFILE.getCode(), HttpStatus.BAD_REQUEST);
        }

        AccountEntity userAccount = new AccountEntity();
        userAccount.setEmail(registerUserDomain.getEmail());
        userAccount.setPassword(new BCryptPasswordEncoder().encode(registerUserDomain.getPassword()));
        userAccount.setRoleId(StringUtils.convertObjectToLongOrNull(registerUserDomain.getRoleId()));
        userAccount = accountRepository.save(userAccount);

        UserEntity userEntity = new UserEntity();
        userEntity.setAccountId(userAccount.getId());
        userEntity.setAvatar(registerUserDomain.getAvatar() == null ? userEntity.getAvatar() : registerUserDomain.getAvatar());
        userEntity.setFullName(registerUserDomain.getFullName());
        userEntity.setBirthday(DateTimeUtils.convertStringToDateOrNull(registerUserDomain.getBirthday(), DateTimeUtils.YYYYMMDD));
        userEntity.setGender(StringUtils.convertStringToIntegerOrNull(registerUserDomain.getGender()));
        userEntity.setPhoneNumber(registerUserDomain.getPhone());
        userEntity = userRepository.save(userEntity);


        LoginUser loginUser = new LoginUser();
        loginUser.setEmail(userAccount.getEmail());
        loginUser.setPassword(registerUserDomain.getPassword());

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse response = new LoginResponse();
        response.setUserId(userEntity.getId());
        response.setEmail(userAccount.getEmail());
        response.setPassword(userAccount.getPassword());
        RoleEntity roleEntity = roleRepository.findById(userAccount.getRoleId()).orElse(null);
        response.setRoles(Arrays.asList(new RoleDomain(roleEntity.getId(), roleEntity.getName())));
        response.setFullName(userEntity.getFullName());
        response.setAccessToken(tokenProvider.generateAccessToken(authentication));
        response.setRefreshToken(tokenProvider.generateRefreshToken(authentication));
        return response;
    }

    @Override
    public void resetPassword(ChangePasswordDomain resetPasswordDomain) {
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        if (resetPasswordDomain.getOldPass().equals(resetPasswordDomain.getNewPass())) {
            throw new CustomException(Error.SAME_PASS.getMessage(), Error.SAME_PASS.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (new BCryptPasswordEncoder().matches(resetPasswordDomain.getOldPass(), accountEntity.getPassword())) {
            throw new CustomException(Error.INVALID_OLD_PASS.getMessage(), Error.INVALID_OLD_PASS.getCode(), HttpStatus.BAD_REQUEST);
        }

        accountEntity.setPassword(new BCryptPasswordEncoder().encode(resetPasswordDomain.getNewPass()));
        accountRepository.save(accountEntity);
    }

    @Override
    public UserInformDomain getProfile() {
        UserInformDomain userInformDomain = new UserInformDomain();
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);

        userInformDomain.setEmail(accountEntity.getEmail());
        userInformDomain.setAvatar(userInformDomain.getAvatar());
        userInformDomain.setBirthday(StringUtils.convertDateToStringFormatyyyyMMdd(userEntity.getBirthday()));
        userInformDomain.setFullName(userEntity.getFullName());
        userInformDomain.setPhone(userEntity.getPhoneNumber());
        userInformDomain.setGender(StringUtils.convertObjectToString(userEntity.getGender()));
        return userInformDomain;
    }

    @Override
    public void sendMailForgotPassword(ForgotPasswordDomain forgotPasswordDomain) {
        AccountEntity accountEntity = accountRepository.findByEmail(forgotPasswordDomain.getEmail());
        if (accountEntity == null) {
            throw new CustomException(Error.EMAIL_NOT_EXIST.getMessage(), Error.EMAIL_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = userRepository.findByAccountId(accountEntity.getId());
        String token = RandomStringUtils.randomAlphabetic(8);
        VerifyEntity verifyEntity = verifyRepository.findByUserId(userEntity.getId());
        if (verifyEntity == null) {
            verifyEntity = new VerifyEntity();
            verifyEntity.setUserId(userEntity.getId());
            verifyEntity.setVerifyResetPassToken(token);
        } else {
            verifyEntity.setVerifyResetPassToken(token);
        }
        verifyRepository.save(verifyEntity);
        sendMailForgotPass(accountEntity.getEmail(), token, userEntity.getFullName());

    }

    public void sendMailForgotPass(String email, String token, String name) {
        SendMailDomain sendMailDomain = new SendMailDomain();
        sendMailDomain.setToEmail(Arrays.asList(email));
        sendMailDomain.setMessageContent("");
        String subject = "Email thực hiện thiết đặt mật khẩu tài khoản CleanMe";
        String template = "forgot-pass-email-template";
        sendMailDomain.setSubject(subject);
        Map<String, Object> paramInfo = new HashMap<>();
        paramInfo.put("token", token);
        name = StringUtils.isEmpty(name) ? "bạn" : name;
        paramInfo.put("name", name);
        sendMailUtils.sendMailWithTemplate(sendMailDomain, template, paramInfo);

    }


    @Override
    public void sendMailVerifyEmail(VerifyEmailDomain verifyEmailDomain) {
        String email = verifyEmailDomain.getEmail();
        if (!StringUtils.isValidString(email)) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        AccountEntity accountEntity = accountRepository.findByEmail(verifyEmailDomain.getEmail());
        if (accountEntity == null) {
            throw new CustomException(Error.EMAIL_NOT_EXIST.getMessage(), Error.EMAIL_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = userRepository.findByAccountId(accountEntity.getId());
        String token = RandomStringUtils.randomAlphabetic(8);
        VerifyEntity verifyEntity = verifyRepository.findByUserId(userEntity.getId());

        if (verifyEntity == null) {
            verifyEntity = new VerifyEntity();
            verifyEntity.setUserId(userEntity.getId());
            verifyEntity.setVerifyEmailToken(token);
        } else {
            if (verifyEntity.getIsVerifyEmail() == 1) {
                throw new CustomException(Error.EMAIL_IS_VERIFY.getMessage(), Error.EMAIL_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
            }
            verifyEntity.setVerifyEmailToken(token);
        }
        verifyRepository.save(verifyEntity);
        sendMailVerifyMail(accountEntity.getEmail(), token, userEntity.getFullName());
    }

    public void sendMailVerifyMail(String email, String token, String name) {
        SendMailDomain sendMailDomain = new SendMailDomain();
        sendMailDomain.setToEmail(Arrays.asList(email));
        sendMailDomain.setMessageContent("");
        String subject = "Email xác thực tài khoản CleanMe";
        String template = "verify-email-template";
        sendMailDomain.setSubject(subject);
        Map<String, Object> paramInfo = new HashMap<>();
        paramInfo.put("token", token);
        name = StringUtils.isEmpty(name) ? "bạn" : name;
        paramInfo.put("name", name);
        sendMailUtils.sendMailWithTemplate(sendMailDomain, template, paramInfo);

    }

    @Override
    public void sendSMSVerifyPhone(VerifyPhoneDomain verifyPhoneDomain) {
        String phoneNumber = verifyPhoneDomain.getPhoneNumber();
        if (!StringUtils.isValidString(phoneNumber)) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if (userEntity == null) {
            throw new CustomException(Error.PHONE_NOT_EXIST.getMessage(), Error.PHONE_NOT_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }

        String token = RandomStringUtils.randomAlphabetic(8);
        VerifyEntity verifyEntity = verifyRepository.findByUserId(userEntity.getId());

        if (verifyEntity == null) {
            verifyEntity = new VerifyEntity();
            verifyEntity.setUserId(userEntity.getId());
            verifyEntity.setVerifyPhoneToken(token);
        } else {
            if (verifyEntity.getIsVerifyPhone() == 1) {
                throw new CustomException(Error.PHONE_IS_VERIFY.getMessage(), Error.PHONE_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
            }
            verifyEntity.setVerifyPhoneToken(token);
        }
        verifyRepository.save(verifyEntity);
        String message = "Mã xác thực tài khoản CleamMe của bạn là: " + token;
        sendSmsUtils.sendSMS("+84" + userEntity.getPhoneNumber().substring(1), message);
    }

    @Override
    public void verifyEmail(VerifyDomain verifyDomain) {
        String token = verifyDomain.getVerifyToken();
        if (!StringUtils.isValidString(token)) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.getOne(UserUtils.getCurrentUserId());

        VerifyEntity verifyEntity = verifyRepository.findByVerifyEmailTokenAndUserId(token, userEntity.getId());

        if (verifyEntity == null) {
            throw new CustomException(Error.PHONE_IS_VERIFY.getMessage(), Error.PHONE_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
        } else {
            verifyEntity.setIsVerifyEmail(1);
        }
        verifyRepository.save(verifyEntity);
    }

    @Override
    public void verifyPhone(VerifyDomain verifyDomain) {
        String token = verifyDomain.getVerifyToken();
        if (!StringUtils.isValidString(token)) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.getOne(UserUtils.getCurrentUserId());

        VerifyEntity verifyEntity = verifyRepository.findByVerifyPhoneTokenAndUserId(token, userEntity.getId());

        if (verifyEntity == null) {
            throw new CustomException(Error.PHONE_IS_VERIFY.getMessage(), Error.PHONE_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
        } else {
            verifyEntity.setIsVerifyPhone(1);
        }
        verifyRepository.save(verifyEntity);
    }

    @Override
    public void resetPassword(ResetPasswordDomain resetPasswordDomain) {
        String token = resetPasswordDomain.getToken();
        String newPassword = resetPasswordDomain.getNewPassword();
        if (!StringUtils.isValidString(token) || !StringUtils.isValidString(newPassword)) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        VerifyEntity verifyEntity = verifyRepository.findByVerifyResetPassToken(token);

        if (verifyEntity == null) {
            throw new CustomException(Error.PHONE_IS_VERIFY.getMessage(), Error.PHONE_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = userRepository.getById(verifyEntity.getUserId());
        if (userEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        AccountEntity accountEntity = accountRepository.getById(userEntity.getAccountId());
        accountEntity.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        verifyRepository.save(verifyEntity);
    }


}
