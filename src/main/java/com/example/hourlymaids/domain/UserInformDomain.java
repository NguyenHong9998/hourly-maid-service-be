package com.example.hourlymaids.domain;

import com.example.hourlymaids.util.StringUtils;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInformDomain {
    private String email;
    private String phone;
    private String password;
    private String fullName;
    private String avatar;
    private String gender;
    private String roleId;
    private String birthday;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isValidUser(UserInformDomain domain) {
        if (StringUtils.isEmpty(domain.getEmail()) || StringUtils.isEmpty(domain.password) || StringUtils.isEmpty(domain.getPhone()) ||
                StringUtils.isEmpty(domain.getFullName()) || StringUtils.isEmpty(domain.getRoleId())) {
            return false;
        }
        return true;
    }
}
