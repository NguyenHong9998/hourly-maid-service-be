package com.example.hourlymaids.constant;

public enum TaskStatus {
    CREATED(1, "Đã tạo"),
    UN_ASSIGNED(2, "Chưa giao NV"),
    CANCELED(3, "Đã huỷ"),
    DONE(4, "Hoàn thành"),
    ASSIGNED(2, "Đã giao NV")
;

    private String value;
    private Integer code;

    TaskStatus(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Integer getCode() {
        return code;
    }

    public static TaskStatus getStatusByCode(Integer code) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static TaskStatus getStatusByValue(String value) {
        for (TaskStatus status : TaskStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
