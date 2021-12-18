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
import com.example.hourlymaids.util.DateTimeUtils;
import com.example.hourlymaids.util.StringUtils;
import com.example.hourlymaids.util.UserUtils;
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

import java.util.Arrays;

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

    @Override
    public LoginResponse checkLogin(LoginUser loginUser) throws Exception {
        LoginResponse response = new LoginResponse();
        if (StringUtils.isEmpty(loginUser.getEmail()) || StringUtils.isEmpty(loginUser.getPass())) {
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

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPass()));
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
    public LoginResponse checkRegister(UserInformDomain registerUserDomain) throws Exception {
        if (StringUtils.isEmpty(registerUserDomain.getEmail()) || StringUtils.isEmpty(registerUserDomain.getPassword())) {
            throw new CustomException(Error.INVALID_EMAIL_OR_PASSWORD.getMessage(), Error.INVALID_EMAIL_OR_PASSWORD.getCode(), HttpStatus.BAD_REQUEST);
        }
        AccountEntity accountEntity = accountRepository.findByEmail(registerUserDomain.getEmail());
        if (accountEntity != null) {
            throw new CustomException(Error.EMAIL_EXIST.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }

        if (!registerUserDomain.isValidUser()) {
            throw new CustomException(Error.INVALID_PROFILE.getMessage(), Error.INVALID_PROFILE.getCode(), HttpStatus.BAD_REQUEST);
        }

        AccountEntity userAccount = new AccountEntity();
        userAccount.setEmail(registerUserDomain.getEmail());
        userAccount.setPassword(new BCryptPasswordEncoder().encode(registerUserDomain.getPassword()));
        userAccount.setRoleId(StringUtils.convertObjectToLongOrNull(registerUserDomain.getRoleId()));
        userAccount = accountRepository.save(userAccount);

        UserEntity userEntity = new UserEntity();
        userEntity.setAccountId(userAccount.getId());
        userEntity.setAvatar(registerUserDomain.getAvatar());
        userEntity.setFullName(registerUserDomain.getFullName());
        userEntity.setBirthday(DateTimeUtils.convertStringToDateOrNull(registerUserDomain.getBirthday(), DateTimeUtils.YYYYMMDD));
        userEntity.setGender(StringUtils.convertStringToIntegerOrNull(registerUserDomain.getGender()));
        userEntity.setPhoneNumber(registerUserDomain.getPhone());
        userEntity = userRepository.save(userEntity);


        LoginUser loginUser = new LoginUser();
        loginUser.setEmail(userAccount.getEmail());
        loginUser.setPass(registerUserDomain.getPassword());

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPass()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse response = new LoginResponse();
        response.setUserId(userEntity.getId());
        response.setEmail(userAccount.getEmail());
        response.setPassword(userAccount.getPassword());
        RoleEntity roleEntity = roleRepository.findById(accountEntity.getRoleId()).orElse(null);
        response.setRoles(Arrays.asList(new RoleDomain(roleEntity.getId(), roleEntity.getName())));
        response.setFullName(userEntity.getFullName());
        response.setAccessToken(tokenProvider.generateAccessToken(authentication));
        response.setRefreshToken(tokenProvider.generateRefreshToken(authentication));
        return response;
    }

    @Override
    public void resetPassword(ResetPasswordDomain resetPasswordDomain) {
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
        userInformDomain.setPassword(accountEntity.getPassword());
        userInformDomain.setAvatar(userInformDomain.getAvatar());
        userInformDomain.setBirthday(StringUtils.convertDateToStringFormatyyyyMMdd(userEntity.getBirthday()));
        userInformDomain.setFullName(userEntity.getFullName());
        userInformDomain.setPhone(userEntity.getPhoneNumber());
        userInformDomain.setGender(StringUtils.convertObjectToString(userEntity.getGender()));

        return userInformDomain;
    }
}
