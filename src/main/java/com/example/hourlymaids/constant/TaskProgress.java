package com.example.hourlymaids.constant;

public enum TaskProgress {
    CREATED(1, "Tạo mới công việc thành công"),
    CANCELED(3, "Huỷ thành công"),
    DONE(4, "Hoàn thành công việc"),
    ASSIGNED(2, "Đã giao việc cho nhân viên thành công");

    private String value;
    private Integer code;

    TaskProgress(Integer code, String value) {
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
