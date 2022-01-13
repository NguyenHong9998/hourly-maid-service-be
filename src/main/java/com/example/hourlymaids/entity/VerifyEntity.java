package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "USER_VERIFY")
@Where(clause = "is_deleted = 0")
public class VerifyEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "USER_VERIFY_SEQ", sequenceName = "USER_VERIFY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "USER_VERIFY_SEQ")
    private Long id;
    @Column(name = "VERIFY_EMAIL_TOKEN")
    private String verifyEmailToken;
    @Column(name = "VERIFY_PHONE_TOKEN")
    private String verifyPhoneToken;
    @Column(name = "VERIFY_RESET_PASS_TOKEN")
    private String verifyResetPassToken;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "IS_VERIFY_EMAIL", columnDefinition = "Numeric(2,0) default '0'")
    protected Integer isVerifyEmail;
    @Column(name = "IS_VERIFY_PHONE", columnDefinition = "Numeric(2,0) default '0'")
    protected Integer isVerifyPhone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVerifyEmailToken() {
        return verifyEmailToken;
    }

    public void setVerifyEmailToken(String verifyEmailToken) {
        this.verifyEmailToken = verifyEmailToken;
    }

    public String getVerifyPhoneToken() {
        return verifyPhoneToken;
    }

    public void setVerifyPhoneToken(String verifyPhoneToken) {
        this.verifyPhoneToken = verifyPhoneToken;
    }

    public String getVerifyResetPassToken() {
        return verifyResetPassToken;
    }

    public void setVerifyResetPassToken(String verifyResetPassToken) {
        this.verifyResetPassToken = verifyResetPassToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getIsVerifyEmail() {
        return isVerifyEmail;
    }

    public void setIsVerifyEmail(Integer isVerifyEmail) {
        this.isVerifyEmail = isVerifyEmail;
    }

    public Integer getIsVerifyPhone() {
        return isVerifyPhone;
    }

    public void setIsVerifyPhone(Integer isVerifyPhone) {
        this.isVerifyPhone = isVerifyPhone;
    }
}
