package com.example.hourlymaids.constant;

public enum FeedbackType {
    FEEDBACK_EMPLOYEE(1, "Đánh giá nhân viên"),
    FEEDBACK_COMPANY(2, "Đánh giá công ty");

    private String value;
    private Integer code;

    FeedbackType(Integer code,String value ){
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }
}
