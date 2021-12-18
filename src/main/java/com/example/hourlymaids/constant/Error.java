package com.example.hourlymaids.constant;

public enum Error {
    BAD_CREDENTIALS("S0001", "Bad credentials"),
    TOKEN_INVALID("S0002", "Expired or invalid JWT token"),
    REQUIRED_FIELD("S0003", "Required"),
    OLD_PASSWORD_IS_WRONG("S0004", "Old password is wrong"),
    NEW_AND_OLD_PASSWORD_IS_SIMILAR("S0005", "New password and old password is similar"),
    PARAMETER_INVALID("S0006", "Parameter invalid"),
    INVALID_USERNAME_OR_PASSWORD("S0007", "Email hoặc m"),
    EXTERNAL_LOGIN_FAIL("S0011", "External login fail"),
    INVALID_EMAIL_OR_PASSWORD("S0007", "Email hoặc mật khẩu không đúng"),
    EMAIL_EXIST("S008", "Email đã tồn tại"),
    INVALID_PROFILE("S009", "Thông tin không hợp lệ"),
    SAME_PASS("S010", "Mật khẩu mới giống với mật khẩu cũ"),
    INVALID_OLD_PASS("S011", "Mật khẩu cũ không đúng");


    private String code;

    private String message;

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    Error(String code, String message) {
        this.code = code;
        this.message = message;
    }
}