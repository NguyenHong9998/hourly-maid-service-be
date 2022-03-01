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
    private UserVerifyRepository verifyRepository;
    @Autowired
    private SendMailUtils sendMailUtils;
    @Autowired
    private SendSmsUtils sendSmsUtils;

    @Autowired
    private FeedbackRepository feedbackRepository;
    @Value("${cms.link}")
    private String link;

    @Autowired
    private UserVerifyRepository userVerifyRepository;

    @Override
    public LoginResponse checkLogin(LoginUser loginUser) throws Exception {
        LoginResponse response = new LoginResponse();
        if (StringUtils.isEmpty(loginUser.getEmail()) || StringUtils.isEmpty(loginUser.getPassword())) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.findByEmail(loginUser.getEmail());

        if (userEntity == null) {
            throw new CustomException("Mật khẩu hoặc tài khoản không đúng!", Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
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
        if (domain.getRoles().get(0).getRoleId() == 3l) {
            VerifyEntity verifyEntity = verifyRepository.findByUserId(domain.getUserId());
            if (verifyEntity != null && verifyEntity.getIsVerifyEmail() != null && verifyEntity.getIsVerifyEmail() != 1) {
                verifyEntity.setIsVerifyEmail(1);
                verifyRepository.save(verifyEntity);
            }else {
                VerifyEntity entity = new VerifyEntity();
                entity.setUserId(domain.getUserId());
                entity.setIsVerifyEmail(1);
                verifyRepository.save(entity);
            }
        }
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

        if (!registerUserDomain.isValidUser(registerUserDomain)) {
            throw new CustomException(Error.INVALID_PROFILE.getMessage(), Error.INVALID_PROFILE.getCode(), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.findByEmail(registerUserDomain.getEmail());
        if (userEntity != null) {
            throw new CustomException(Error.EMAIL_EXIST.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }

        userEntity = new UserEntity();
        userEntity.setFullName(registerUserDomain.getFullName());
        userEntity.setPhoneNumber(registerUserDomain.getPhone());
        RoleEntity roleEntity = roleRepository.findByName(registerUserDomain.getRoleId());
        userEntity.setRoleId(roleEntity.getId());
        userEntity.setEmail(registerUserDomain.getEmail());
        userEntity.setPassword(new BCryptPasswordEncoder().encode(registerUserDomain.getPassword()));
        userEntity.setAvatar("https://www.sibberhuuske.nl/wp-content/uploads/2016/10/default-avatar.png");
        userEntity = userRepository.save(userEntity);

        LoginUser loginUser = new LoginUser();
        loginUser.setEmail(userEntity.getEmail());
        loginUser.setPassword(registerUserDomain.getPassword());

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginUser, loginUser.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LoginResponse response = new LoginResponse();
        response.setUserId(userEntity.getId());
        response.setEmail(userEntity.getEmail());
        response.setRoles(Arrays.asList(new RoleDomain(roleEntity.getId(), roleEntity.getName())));
        response.setFullName(userEntity.getFullName());
        response.setAccessToken(tokenProvider.generateAccessToken(authentication));
        response.setRefreshToken(tokenProvider.generateRefreshToken(authentication));
        return response;
    }

    @Override
    public void resetPassword(ChangePasswordDomain resetPasswordDomain) {
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        if (resetPasswordDomain.getOldPass().equals(resetPasswordDomain.getNewPass())) {
            throw new CustomException(Error.SAME_PASS.getMessage(), Error.SAME_PASS.getCode(), HttpStatus.BAD_REQUEST);
        }
        if (new BCryptPasswordEncoder().matches(resetPasswordDomain.getOldPass(), userEntity.getPassword())) {
            throw new CustomException(Error.INVALID_OLD_PASS.getMessage(), Error.INVALID_OLD_PASS.getCode(), HttpStatus.BAD_REQUEST);
        }

        userEntity.setPassword(new BCryptPasswordEncoder().encode(resetPasswordDomain.getNewPass()));
        userRepository.save(userEntity);
    }

    @Override
    public UserInformDomain getProfile() {
        UserInformDomain userInformDomain = new UserInformDomain();
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);

        userInformDomain.setEmail(userEntity.getEmail());
        userInformDomain.setAvatar(userInformDomain.getAvatar());
        userInformDomain.setBirthday(StringUtils.convertDateToStringFormatyyyyMMdd(userEntity.getBirthday()));
        userInformDomain.setFullName(userEntity.getFullName());
        userInformDomain.setPhone(userEntity.getPhoneNumber());
        userInformDomain.setGender(StringUtils.convertObjectToString(userEntity.getGender()));
        return userInformDomain;
    }

    @Override
    public void sendMailForgotPassword(ForgotPasswordDomain forgotPasswordDomain) {

        UserEntity userEntity = userRepository.findByEmail(forgotPasswordDomain.getEmail());
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
        sendMailForgotPass(userEntity.getEmail(), token, userEntity.getFullName());

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

        UserEntity userEntity = userRepository.findByEmail(email);
        String token = RandomStringUtils.randomAlphabetic(8);
        VerifyEntity verifyEntity = verifyRepository.findByUserId(userEntity.getId());

        if (verifyEntity == null) {
            verifyEntity = new VerifyEntity();
            verifyEntity.setUserId(userEntity.getId());
            verifyEntity.setVerifyEmailToken(token);
        } else {
            if (verifyEntity.getIsVerifyEmail() != null && verifyEntity.getIsVerifyEmail() == 1) {
                throw new CustomException(Error.EMAIL_IS_VERIFY.getMessage(), Error.EMAIL_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
            }
            verifyEntity.setVerifyEmailToken(token);
        }
        verifyRepository.save(verifyEntity);
        sendMailVerifyMail(userEntity.getEmail(), token, userEntity.getFullName());
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

        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);

        String token = RandomStringUtils.randomAlphabetic(8);
        VerifyEntity verifyEntity = verifyRepository.findByUserId(userEntity.getId());

        if (verifyEntity == null) {
            verifyEntity = new VerifyEntity();
            verifyEntity.setUserId(userEntity.getId());
            verifyEntity.setVerifyPhoneToken(token);
        } else {
            if (verifyEntity.getIsVerifyPhone() != null && verifyEntity.getIsVerifyPhone() == 1) {
                throw new CustomException(Error.PHONE_IS_VERIFY.getMessage(), Error.PHONE_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
            }
            verifyEntity.setVerifyPhoneToken(token);
        }
        verifyRepository.save(verifyEntity);
        String message = "Mã xác thực tài khoản CleamMe của bạn là: " + token;
        sendSmsUtils.sendSMS("+84" + userEntity.getPhoneNumber().substring(0), message);
    }

    @Override
    public void verifyEmail(VerifyDomain verifyDomain) {
        String token = verifyDomain.getVerifyToken();
        if (!StringUtils.isValidString(token)) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage(), Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);

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

        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);

        VerifyEntity verifyEntity = verifyRepository.findByVerifyPhoneTokenAndUserId(token, userEntity.getId());

        if (verifyEntity == null) {
            throw new CustomException("Mã xác thực không đúng, hãy kiểm tra lại!", Error.PHONE_IS_VERIFY.getCode(), HttpStatus.BAD_REQUEST);
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
        userEntity.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepository.save(userEntity);
        verifyRepository.save(verifyEntity);
    }

    @Override
    public CommonInformDomain getUserCommonInform() {
        CommonInformDomain domain = new CommonInformDomain();
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        domain.setAvatar(domain.getAvatar());
        RoleEntity roleEntity = roleRepository.findById(userEntity.getRoleId()).orElse(null);
        domain.setRole(roleEntity.getName());
        domain.setStatus(EmployeeStatus.getEmployeeStatusByCode(userEntity.getStatus()) == null ? "" : EmployeeStatus.getEmployeeStatusByCode(userEntity.getStatus()).getValue());
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

        UserRole userRole = UserRole.getRoleByValue(role);
        RoleEntity roleEntity = roleRepository.findByName(userRole.getName());
        userEntity.setRoleId(roleEntity.getId());
        userRepository.save(userEntity);
    }

    @Override
    public UserPersonalInformDomain getUserPersonalInform() {
        UserPersonalInformDomain domain = new UserPersonalInformDomain();
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        domain.setAddress(userEntity.getAddress());
        domain.setGender(Gender.getEmployeeStatusByCode(userEntity.getGender()).getValue());
        domain.setEmail(userEntity.getEmail());
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
        UserEntity checkExistAcc = userRepository.findByEmail(email);
        if (checkExistAcc != null && checkExistAcc.getId() != userEntity.getId()) {
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
        userEntity.setEmail(email);
        userRepository.save(userEntity);
    }

    @Override
    public void changePassword(ChangePasswordDomain domain) {
        UserEntity userEntity = userRepository.findById(UserUtils.getCurrentUserId()).orElse(null);
        if (!(new BCryptPasswordEncoder().matches(domain.getOldPass(), userEntity.getPassword()))) {
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
        userEntity.setPassword(new BCryptPasswordEncoder().encode(domain.getNewPass()));
        userRepository.save(userEntity);
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
        String currentRole = UserUtils.getCurrentUser().getRoles().get(0).getName();
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
            String roleName = StringUtils.convertObjectToString(object[1]);
            UserRole userRole = UserRole.getRoleByName(roleName);
            domain.setEmail(userEntity.getEmail());
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
        UserEntity checkExistAcc = userRepository.findByEmail(email);
        if (checkExistAcc != null) {
            throw new CustomException(Error.EMAIL_EXIST.getMessage(), Error.EMAIL_EXIST.getCode(), HttpStatus.BAD_REQUEST);
        }
        RoleEntity roleEntity = roleRepository.findByName(userRole.getName());
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        String pass = RandomStringUtils.randomAlphanumeric(8);
        String password = new BCryptPasswordEncoder().encode(pass);
        userEntity.setRoleId(roleEntity.getId());
        userEntity.setPassword(password);
        userEntity.setFullName(name);
        userEntity.setPhoneNumber(phone);
        userEntity.setStatus(EmployeeStatus.ACTIVE.getCode());
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
    public void changeStatusEmployee(ChangeStatusDomain domain) {
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
        domain.setAvatar(domain.getAvatar());
        RoleEntity roleEntity = roleRepository.findById(userEntity.getRoleId()).orElse(null);
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
        domain.setAddress(userEntity.getAddress());
        domain.setGender(Gender.getEmployeeStatusByCode(userEntity.getGender()).getValue());
        domain.setEmail(userEntity.getEmail());
        domain.setPhone(userEntity.getPhoneNumber());
        domain.setIdCard(userEntity.getIdCard());
        domain.setName(userEntity.getFullName());
        domain.setDateOfBirth(DateTimeUtils.convertDateToStringOrEmpty(userEntity.getBirthday(), DateTimeUtils.YYYYMMDD));
        VerifyEntity verifyEntity = verifyRepository.findByUserId(userId);
        if (verifyEntity == null) {
            domain.setVerifyEmail(false);
            domain.setVerifyPhone(false);
        } else {
            domain.setVerifyEmail(verifyEntity.getIsVerifyEmail() != null && verifyEntity.getIsVerifyEmail() == 1 ? true : false);
            domain.setVerifyPhone(verifyEntity.getIsVerifyPhone() != null && verifyEntity.getIsVerifyPhone() == 1 ? true : false);
        }
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

        UserRole userRole = UserRole.getRoleByValue(role);
        RoleEntity roleEntity = roleRepository.findByName(userRole.getName());
        userEntity.setRoleId(roleEntity.getId());
        userRepository.save(userEntity);
    }

    @Override
    public void updateUserPersonalInform(UserPersonalInformDomain domain, String id) {
        UserEntity userEntity = userRepository.findById(StringUtils.convertObjectToLongOrNull(id)).orElse(null);
        String email = domain.getEmail();
        UserEntity checkExistAcc = userRepository.findByEmail(email);
        if (checkExistAcc != null && checkExistAcc.getId() != UserUtils.getCurrentUserId()) {
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
        userEntity.setEmail(email);
        userRepository.save(userEntity);
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

    @Override
    public OverviewOfEmployeeDomain getOverviewOfEmployee(String startDate, String endDate) {
        OverviewOfEmployeeDomain overviewOfEmployeeDomain = new OverviewOfEmployeeDomain();
        Long employeeId = UserUtils.getCurrentUserId();
        List<FeedbackEntity> feedbackEntities = feedbackRepository.findAll();
        List<UserEntity> userEntities = userRepository.findAll().stream().filter(t -> t.getRoleId() == 3).collect(Collectors.toList());
        overviewOfEmployeeDomain.setTotalFeedUser(StringUtils.convertObjectToString(feedbackRepository.findAllFeedbackDistinctByEmployeeId(employeeId).size()));
        List<UserStar> userStars = feedbackEntities.stream().collect(Collectors.groupingBy(t -> t.getEmployeeId())).entrySet().stream().map(entry -> {
            UserStar userStar = new UserStar();
            userStar.setUser(entry.getKey());
            userStar.setStar(entry.getValue().stream().map(e -> e.getRateNumber()).reduce(Integer::sum).orElse(0));
            return userStar;
        }).sorted(Comparator.comparing(UserStar::getStar).reversed()).collect(Collectors.toList());
        int position = userStars.size() + 1;
        for (int i = 0; i < userStars.size(); i++) {
            if (userStars.get(i).getUser() == employeeId) {
                position = i + 1;
                break;
            }
        }
        overviewOfEmployeeDomain.setPosition(position + "/" + userEntities.size());
        overviewOfEmployeeDomain.setTotalStar(StringUtils.convertObjectToString(userStars.stream().filter(t -> t.getUser() == employeeId).map(t -> t.getStar()).reduce(Integer::sum).orElse(0)));
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);

        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<FeedbackEntity> feedbackEntitiesOfEmployee = feedbackRepository.findByEmployeeId(employeeId);

        List<ItemOnDateDomain> itemList = new ArrayList<>();
        for (Date date : dateList) {
            ItemOnDateDomain item = new ItemOnDateDomain();
            item.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
            Integer num = feedbackEntitiesOfEmployee.stream().filter(t -> {
                String createDate = StringUtils.convertDateToStringFormatPattern(t.getCreatedDate(), DateTimeUtils.YYYYMMDD);
                Date cd = DateTimeUtils.convertStringToDateOrNull(createDate, DateTimeUtils.YYYYMMDD);
                return cd.getTime() == date.getTime();
            }).collect(Collectors.toList()).size();
            item.setNumber(StringUtils.convertObjectToString(num));
            itemList.add(item);
        }
        overviewOfEmployeeDomain.setDetails(itemList);

        return overviewOfEmployeeDomain;
    }

    @Override
    public List<ItemOnDateDomain> getOverViewDetailOfEmployee(String startDate, String endDate) {
        Date start = DateTimeUtils.convertStringToDateOrNull(startDate, DateTimeUtils.YYYYMMDD);
        Date end = DateTimeUtils.convertStringToDateOrNull(endDate, DateTimeUtils.YYYYMMDD);

        List<Date> dateList = DateTimeUtils.getDatesBetweenDateRange(start, end);
        List<FeedbackEntity> feedbackEntitiesOfEmployee = feedbackRepository.findByEmployeeId(UserUtils.getCurrentUserId());

        List<ItemOnDateDomain> itemList = new ArrayList<>();
        for (Date date : dateList) {
            ItemOnDateDomain item = new ItemOnDateDomain();
            item.setDate(StringUtils.convertDateToStringFormatPattern(date, DateTimeUtils.DDMMYYYY));
            Integer num = feedbackEntitiesOfEmployee.stream().filter(t -> {
                String createDate = StringUtils.convertDateToStringFormatPattern(t.getCreatedDate(), DateTimeUtils.YYYYMMDD);
                Date cd = DateTimeUtils.convertStringToDateOrNull(createDate, DateTimeUtils.YYYYMMDD);
                return cd.getTime() == date.getTime();
            }).collect(Collectors.toList()).size();
            item.setNumber(StringUtils.convertObjectToString(num));
            itemList.add(item);
        }

        return itemList;
    }

    @Override
    public void updateClientCommonInform(ClientCommonInform domain) {
        Long userId = UserUtils.getCurrentUserId();
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        userEntity.setAvatar(domain.getAvatar());
        userEntity.setGender(Gender.getEmployeeStatusByValue(domain.getGender()).getCode());
        userEntity.setBirthday(DateTimeUtils.convertStringToDateOrNull(domain.getDateOfBirth(), DateTimeUtils.YYYYMMDD));
        userRepository.save(userEntity);
    }

    @Override
    public ClientCommonInform getClientInform() {
        Long userId = UserUtils.getCurrentUserId();
        UserEntity userEntity = userRepository.findById(userId).orElse(null);

        ClientCommonInform inform = new ClientCommonInform();
        inform.setAvatar(userEntity.getAvatar());
        inform.setEmail(userEntity.getEmail());
        inform.setGender(Gender.getEmployeeStatusByCode(userEntity.getGender()).getValue());
        inform.setName(userEntity.getFullName());
        inform.setPhone(userEntity.getPhoneNumber());
        inform.setEmail(userEntity.getEmail());
        inform.setDateOfBirth(DateTimeUtils.convertDateToStringOrEmpty(userEntity.getBirthday(), DateTimeUtils.YYYYMMDD));
        VerifyEntity verifyEntity = userVerifyRepository.findByUserId(userId);
        inform.setVerifyEmail((verifyEntity == null || verifyEntity.getIsVerifyEmail() == null || (verifyEntity.getIsVerifyEmail() != null && verifyEntity.getIsVerifyEmail() == 0)) ? false : true);
        inform.setVerifyPhone((verifyEntity == null || verifyEntity.getIsVerifyPhone() == null || (verifyEntity.getIsVerifyPhone() != null && verifyEntity.getIsVerifyPhone() == 0)) ? false : true);
        return inform;
    }
}

class UserStar {
    private Long user;
    private Integer star;

    public UserStar() {
    }

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
