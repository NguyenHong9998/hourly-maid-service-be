package com.example.hourlymaids.service;

import com.example.hourlymaids.config.CustomAuthenticationProvider;
import com.example.hourlymaids.config.ResponseDataAPI;
import com.example.hourlymaids.config.TokenProvider;
import com.example.hourlymaids.constant.*;
import com.example.hourlymaids.constant.Error;
import com.example.hourlymaids.domain.*;
import com.example.hourlymaids.entity.*;
import com.example.hourlymaids.repository.*;
import com.example.hourlymaids.util.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Value("${cms.link}")
    private String link;

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

    @Override
    public CommonInformDomain getUserCommonInform() {
        CommonInformDomain domain = new CommonInformDomain();
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        AccountEntity accountEntity = accountRepository.getById(userEntity.getId());
        domain.setAvatar(domain.getAvatar());
        RoleEntity roleEntity = roleRepository.findById(accountEntity.getRoleId()).orElse(null);
        domain.setRole(roleEntity.getName());
        domain.setStatus(EmployeeStatus.getEmployeeStatusByCode(userEntity.getStatus()).getValue());
        domain.setAvatar(userEntity.getAvatar());
        return domain;
    }

    @Override
    public void updateUserCommonInform(CommonInformDomain domain) {
        String avatar = domain.getAvatar();
        String role = domain.getRole();
        String status = domain.getStatus();
        EmployeeStatus employeeStatus = EmployeeStatus.getEmployeeStatusByValue(status);

        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        userEntity.setAvatar(avatar);
        userEntity.setStatus(employeeStatus.getCode());
        userRepository.save(userEntity);

        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        UserRole userRole = UserRole.getRoleByValue(role);
        RoleEntity roleEntity = roleRepository.findByName(userRole.getName());
        accountEntity.setRoleId(roleEntity.getId());
        accountRepository.save(accountEntity);
    }

    @Override
    public UserPersonalInformDomain getUserPersonalInform() {
        UserPersonalInformDomain domain = new UserPersonalInformDomain();
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        domain.setAddress(userEntity.getAddress());
        domain.setGender(Gender.getEmployeeStatusByCode(userEntity.getGender()).getValue());
        domain.setEmail(accountEntity.getEmail());
        domain.setPhone(userEntity.getPhoneNumber());
        domain.setIdCard(userEntity.getIdCard());
        domain.setName(userEntity.getFullName());
        domain.setDateOfBirth(DateTimeUtils.convertDateToStringOrEmpty(userEntity.getBirthday(), DateTimeUtils.YYYYMMDD));

        return domain;
    }

    @Override
    public void updateUserPersonalInform(UserPersonalInformDomain domain) {
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        String email = domain.getEmail();
        AccountEntity checkExistAcc = accountRepository.findByEmail(email);
        if (checkExistAcc != null) {
            throw new CustomException(Error.EMAIL_EXIST.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        String name = domain.getName();
        Integer gender = Gender.getEmployeeStatusByValue(domain.getGender()).getCode();
        String phone = domain.getPhone();
        String idCard = domain.getIdCard();
        String address = domain.getAddress();
        Date dateOfBirth = DateTimeUtils.convertStringToDateOrNull(domain.getDateOfBirth(), DateTimeUtils.YYYYMMDD);

        userEntity.setAddress(address);
        userEntity.setIdCard(idCard);
        userEntity.setBirthday(dateOfBirth);
        userEntity.setGender(gender);
        userEntity.setPhoneNumber(phone);
        userEntity.setFullName(name);

        userRepository.save(userEntity);

        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        accountEntity.setEmail(email);
        accountRepository.save(accountEntity);

    }

    @Override
    public void changePassword(ChangePasswordDomain domain) {
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        if (!(new BCryptPasswordEncoder().matches(domain.getOldPass(), accountEntity.getPassword()))) {
            throw new CustomException(Error.INVALID_OLD_PASS.getMessage(), Error.INVALID_OLD_PASS.getCode(), HttpStatus.BAD_REQUEST);
        }
        String oldPass = domain.getOldPass();
        String newPass = domain.getNewPass();
        String confirmPass = domain.getConfirmNewPass();
        if (oldPass.equals(newPass)) {
            throw new CustomException(Error.SAME_PASS.getMessage(), Error.SAME_PASS.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (!newPass.equals(confirmPass)) {
            throw new CustomException(Error.INVALID_CONFIRM_PASS.getMessage(), Error.INVALID_CONFIRM_PASS.getCode(), HttpStatus.BAD_REQUEST);
        }
        accountEntity.setPassword(new BCryptPasswordEncoder().encode(domain.getNewPass()));
        accountRepository.save(accountEntity);
    }

    @Override
    public ResponseDataAPI getListEmployee(GetListRequest request) {
        List<String> columnSort = Arrays.asList(ColumnSortUser.NAME.getName(), ColumnSortUser.STATUS.getName(), ColumnSortUser.EMAIL.getName(), ColumnSortUser.ROLE.getName());
        Pageable pageable = null;

        if (columnSort.contains(request.getColumnSort())) {
            if (ColumnSortUser.NAME.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortUser.NAME.getValue());
            } else if (ColumnSortUser.STATUS.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortUser.STATUS.getValue());
            } else if (ColumnSortUser.ROLE.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortUser.ROLE.getValue());
            } else if (ColumnSortUser.EMAIL.getName().equals(request.getColumnSort())) {
                request.setColumnSort(ColumnSortUser.EMAIL.getValue());
            }
            pageable = getPageable(request, pageable);
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(), Sort.by(ColumnSortUser.CREATED_DATE.getValue()).descending());
        }
        String valueSearch = StringUtils.replaceSpecialCharacter(request.getValueSearch()).toUpperCase();

        Page<Object[]> entities;
        String role = request.getStatus();
        if (StringUtils.isEmpty(role)) {
            entities = userRepository.findAllUser(valueSearch, pageable);
        } else {
            UserRole userRole = UserRole.getRoleByValue(role);
            RoleEntity entity = roleRepository.findByName(userRole.getName());

            if (entity == null) {
                entities = userRepository.findAllUser(valueSearch, pageable);
            } else {
                entities = userRepository.findAllUserAndStatus(entity.getId(), valueSearch, pageable);
            }
        }


        List<Object> result = entities.stream().map(object -> {
            EmployeeListDomain domain = new EmployeeListDomain();
            UserEntity userEntity = (UserEntity) object[0];
            String email = StringUtils.convertObjectToString(object[1]);
            String roleName = StringUtils.convertObjectToString(object[2]);
            UserRole userRole = UserRole.getRoleByName(roleName);
            domain.setEmail(email);
            domain.setRole(userRole.getValue());
            domain.setId(userEntity.getId().toString());
            domain.setPhone(userEntity.getPhoneNumber());
            domain.setName(userEntity.getFullName());
            domain.setAvatar(userEntity.getAvatar());
            domain.setStatus(EmployeeStatus.getEmployeeStatusByCode(userEntity.getStatus()).getValue());
            return domain;
        }).collect(Collectors.toList());

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(result);
        responseDataAPI.setTotalRows(entities.getTotalElements());

        return responseDataAPI;
    }

    private Pageable getPageable(GetListRequest request, Pageable pageable) {
        if (ConstantDefine.SORT_ASC.equals(request.getTypeSort())) {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.asc(request.getColumnSort())));
        } else if (ConstantDefine.SORT_DESC.equals(request.getTypeSort())) {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.desc(request.getColumnSort())));
        } else {
            pageable = PageRequest.of(request.getOffset(), request.getLimit(),
                    Sort.by(Sort.Order.desc(ColumnSortNotify.CREATE_DATE.getValue())));
        }
        return pageable;
    }

    @Override
    public void createUser(EmployeeListDomain domain) {
        UserEntity userEntity = new UserEntity();
        String email = domain.getEmail();
        String phone = domain.getPhone();
        String name = domain.getName();
        String role = domain.getRole();
        UserRole userRole = UserRole.getRoleByValue(role);
        String avatar = StringUtils.isEmpty(domain.getAvatar()) ? "https://www.sibberhuuske.nl/wp-content/uploads/2016/10/default-avatar.png" : domain.getAvatar();
        if (StringUtils.isEmpty(email)) {
            throw new CustomException(Error.EMAIL_EMPTY.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(phone)) {
            throw new CustomException(Error.PHONE_EMPTY.getMessage(), Error.PHONE_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isEmpty(name)) {
            throw new CustomException(Error.NAME_EMPTY.getMessage(), Error.NAME_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (userRole == null) {
            throw new CustomException(Error.ROLE_EMPTY.getMessage(), Error.ROLE_EMPTY.getCode(), HttpStatus.BAD_REQUEST);
        }
        AccountEntity checkExistAcc = accountRepository.findByEmail(email);
        if (checkExistAcc != null) {
            throw new CustomException(Error.EMAIL_EXIST.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        RoleEntity roleEntity = roleRepository.findByName(userRole.getName());
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setEmail(email);
        String pass = RandomStringUtils.randomAlphanumeric(8);
        String password = new BCryptPasswordEncoder().encode(pass);
        accountEntity.setRoleId(roleEntity.getId());
        accountEntity.setPassword(password);
        accountEntity = accountRepository.save(accountEntity);
        userEntity.setFullName(name);
        userEntity.setPhoneNumber(phone);
        userEntity.setStatus(EmployeeStatus.ACTIVE.getCode());
        userEntity.setAccountId(accountEntity.getId());
        userEntity.setAvatar(avatar);
        userRepository.save(userEntity);
        sendMailToClient(email, password, name, roleEntity.getName());
    }

    void sendMailToClient(String email, String password, String name, String role) {
        SendMailDomain sendMailDomain = new SendMailDomain();
        sendMailDomain.setToEmail(Arrays.asList(email));
        sendMailDomain.setMessageContent("");
        String subject = "CleanMe gửi bạn thông tin tài khoản đăng nhập";
        String template = "send-mail-create-user";
        sendMailDomain.setSubject(subject);
        Map<String, Object> paramInfo = new HashMap<>();
        paramInfo.put("email", email);
        name = StringUtils.isEmpty(name) ? "bạn" : name;
        paramInfo.put("username", name);
        paramInfo.put("password", password);
        paramInfo.put("linkCms", link);
        paramInfo.put("role", role);
        sendMailUtils.sendMailWithTemplate(sendMailDomain, template, paramInfo);
    }

    @Override
    public void changeStatusEmployee(ChangeStatusEmployeeDomain domain) {
        Long id = StringUtils.convertObjectToLongOrNull(domain.getId());
        EmployeeStatus employeeStatus = EmployeeStatus.getEmployeeStatusByValue(domain.getStatus());
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        userEntity.setStatus(employeeStatus.getCode());
        userRepository.save(userEntity);
    }

    @Override
    public CommonInformDomain getUserCommonInforById(String id) {
        CommonInformDomain domain = new CommonInformDomain();
        Long userId = StringUtils.convertObjectToLongOrNull(id);
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        AccountEntity accountEntity = accountRepository.getById(userEntity.getId());
        domain.setAvatar(domain.getAvatar());
        RoleEntity roleEntity = roleRepository.findById(accountEntity.getRoleId()).orElse(null);
        domain.setRole(roleEntity.getName());
        domain.setStatus(EmployeeStatus.getEmployeeStatusByCode(userEntity.getStatus()).getValue());
        domain.setAvatar(userEntity.getAvatar());
        return domain;
    }

    @Override
    public UserPersonalInformDomain getUserPersonalInformById(String id) {
        UserPersonalInformDomain domain = new UserPersonalInformDomain();
        Long userId = StringUtils.convertObjectToLongOrNull(id);
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        domain.setAddress(userEntity.getAddress());
        domain.setGender(Gender.getEmployeeStatusByCode(userEntity.getGender()).getValue());
        domain.setEmail(accountEntity.getEmail());
        domain.setPhone(userEntity.getPhoneNumber());
        domain.setIdCard(userEntity.getIdCard());
        domain.setName(userEntity.getFullName());
        domain.setDateOfBirth(DateTimeUtils.convertDateToStringOrEmpty(userEntity.getBirthday(), DateTimeUtils.YYYYMMDD));

        return domain;
    }

    @Override
    public void updateUserCommonInformById(CommonInformDomain domain, String id) {
        String avatar = domain.getAvatar();
        String role = domain.getRole();
        String status = domain.getStatus();
        EmployeeStatus employeeStatus = EmployeeStatus.getEmployeeStatusByValue(status);
        UserEntity userEntity = userRepository.findById(StringUtils.convertObjectToLongOrNull(id)).orElse(null);
        userEntity.setAvatar(avatar);
        userEntity.setStatus(employeeStatus.getCode());
        userRepository.save(userEntity);

        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        UserRole userRole = UserRole.getRoleByValue(role);
        RoleEntity roleEntity = roleRepository.findByName(userRole.getName());
        accountEntity.setRoleId(roleEntity.getId());
        accountRepository.save(accountEntity);
    }

    @Override
    public void updateUserPersonalInform(UserPersonalInformDomain domain, String id) {
        UserEntity userEntity = userRepository.findById(StringUtils.convertObjectToLongOrNull(id)).orElse(null);
        String email = domain.getEmail();
        AccountEntity checkExistAcc = accountRepository.findByEmail(email);
        if (checkExistAcc != null) {
            throw new CustomException(Error.EMAIL_EXIST.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        String name = domain.getName();
        Integer gender = Gender.getEmployeeStatusByValue(domain.getGender()).getCode();
        String phone = domain.getPhone();
        String idCard = domain.getIdCard();
        String address = domain.getAddress();
        Date dateOfBirth = DateTimeUtils.convertStringToDateOrNull(domain.getDateOfBirth(), DateTimeUtils.YYYYMMDD);

        userEntity.setAddress(address);
        userEntity.setIdCard(idCard);
        userEntity.setBirthday(dateOfBirth);
        userEntity.setGender(gender);
        userEntity.setPhoneNumber(phone);
        userEntity.setFullName(name);

        userRepository.save(userEntity);

        AccountEntity accountEntity = accountRepository.findById(userEntity.getAccountId()).orElse(null);
        accountEntity.setEmail(email);
        accountRepository.save(accountEntity);
    }

    @Override
    public List<UserInformDomain> getOveriewDetailOfFeedbackuser(String startDate, String endDate) {
        List<UserEntity> userEntities = userRepository.findUserHasStatusNotBlock();
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAll();
        List<UserInformDomain> details = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserInformDomain userInformDomain = new UserInformDomain();
            Integer totalStar = feedbackEntities.stream().filter(t -> {
                String createDate = StringUtils.convertDateToStringFormatPattern(t.getCreatedDate(), DateTimeUtils.YYYYMMDD);
                Date cd = DateTimeUtils.convertStringToDateOrNull(createDate, DateTimeUtils.YYYYMMDD);
                return cd.getTime() >= start.getTime() && cd.getTime() <= end.getTime() && t.getEmployeeId() == userEntity.getId();
            }).map(t -> t.getRateNumber()).reduce(Integer::sum).get();
            userInformDomain.setFullName(userEntity.getFullName());
            userInformDomain.setNumStar(totalStar == 0 ? "0" : StringUtils.convertObjectToString(totalStar));
            details.add(userInformDomain);
        }
        return details;
    }

    @Override
    public UserOverviewDomain getOveriewOfFeedbackUser(String startDate, String endDate) {
        UserOverviewDomain overviewUserDomain = new UserOverviewDomain();
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAll();

        Integer numOfUser = feedbackEntities.stream().map(t -> t.getUserId()).distinct().collect(Collectors.toList()).size();
        overviewUserDomain.setNumOfUser(StringUtils.convertObjectToString(numOfUser));

        Map.Entry<Long, Integer> maxUser = feedbackEntities.stream().map(t -> new UserStar(t.getEmployeeId(), t.getRateNumber()))
                .collect(Collectors.groupingBy(t -> t.getUser(), Collectors.summingInt(t -> t.getStar())))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).findFirst().orElse(null);
        UserInformDomain maxUserInform = new UserInformDomain();

        if (maxUser != null) {
            UserEntity userEntity = userRepository.findById(maxUser.getKey()).orElse(null);
            maxUserInform.setAvatar(userEntity.getAvatar());
            maxUserInform.setFullName(userEntity.getFullName());
            maxUserInform.setNumStar(StringUtils.convertObjectToString(maxUser.getValue()));
        }

        Map.Entry<Long, Integer> minUser = feedbackEntities.stream().map(t -> new UserStar(t.getEmployeeId(), t.getRateNumber()))
                .collect(Collectors.groupingBy(t -> t.getUser(), Collectors.summingInt(t -> t.getStar())))
                .entrySet().stream().sorted(Map.Entry.comparingByValue()).findFirst().orElse(null);
        UserInformDomain minUserInform = new UserInformDomain();

        if (maxUser != null) {
            UserEntity userEntity = userRepository.findById(minUser.getKey()).orElse(null);
            minUserInform.setAvatar(userEntity.getAvatar());
            minUserInform.setFullName(userEntity.getFullName());
            minUserInform.setNumStar(StringUtils.convertObjectToString(minUser.getValue()));
        }
        overviewUserDomain.setMaxUser(maxUserInform);
        overviewUserDomain.setMinUser(minUserInform);

        List<UserEntity> userEntities = userRepository.findUserHasStatusNotBlock();
        List<UserInformDomain> details = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserInformDomain userInformDomain = new UserInformDomain();
            Integer totalStar = feedbackEntities.stream().filter(t -> {
                String createDate = StringUtils.convertDateToStringFormatPattern(t.getCreatedDate(), DateTimeUtils.YYYYMMDD);
                Date cd = DateTimeUtils.convertStringToDateOrNull(createDate, DateTimeUtils.YYYYMMDD);
                return cd.getTime() >= start.getTime() && cd.getTime() <= end.getTime() && t.getEmployeeId() == userEntity.getId();
            }).map(t -> t.getRateNumber()).reduce(Integer::sum).orElse(null);
            userInformDomain.setFullName(userEntity.getFullName());
            userInformDomain.setNumStar((totalStar == null || totalStar == 0) ? "0" : StringUtils.convertObjectToString(totalStar));
            details.add(userInformDomain);
        }
        overviewUserDomain.setDetails(details);
        return overviewUserDomain;
    }
}

class UserStar {
    private Long user;
    private Integer star;

    public UserStar(Long user, Integer star) {
        this.user = user;
        this.star = star;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Integer getStar() {
        return star;
    }

    public void setStar(Integer star) {
        this.star = star;
    }
}
