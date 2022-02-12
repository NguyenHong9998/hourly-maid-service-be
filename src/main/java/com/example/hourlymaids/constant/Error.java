package com.example.hourlymaids.constant;

public enum Error {
    BAD_CREDENTIALS("S0001", "Bad credentials"),
    TOKEN_INVALID("S0002", "Expired or invalid JWT token"),
    REQUIRED_FIELD("S0003", "Required"),
    OLD_PASSWORD_IS_WRONG("S0004", "Old password is wrong"),
    NEW_AND_OLD_PASSWORD_IS_SIMILAR("S0005", "New password and old password is similar"),
    PARAMETER_INVALID("S0006", "Parameter invalid"),
    EXTERNAL_LOGIN_FAIL("S0011", "External login fail"),
    INVALID_EMAIL_OR_PASSWORD("S0007", "Email hoặc mật khẩu không đúng"),
    EMAIL_EXIST("S008", "Email đã tồn tại"),
    INVALID_PROFILE("S009", "Thông tin không hợp lệ"),
    SAME_PASS("S010", "Mật khẩu mới giống với mật khẩu cũ, hãy thử lại!"),
    INVALID_OLD_PASS("S011", "Mật khẩu cũ không đúng"),
    EMAIL_NOT_EXIST("S012", "Email không tồn tại"),
    CANT_SEND_EMAIL("S013", "Không thể gửi mail"),
    PHONE_NOT_EXIST("S014", "Số điện thoại không đúng"),
    PHONE_IS_VERIFY("S015", "Số điện thoại của bạn đã được xác thực"),
    EMAIL_IS_VERIFY("S016", "Email của bạn đã được xác thực"),
    SERVICES_NOT_EXIST("S017", "Không tìm thấy dịch vụ"),
    DUPLICATE_SERVICE_NAME("S018", "Tên dịch vụ trống hoặc đã tồn tại"),
    TITLE_EMPTY("S019", "Tiêu đề trống, vui lòng thử lại"),
    START_TIME_OR_DATE_TIME_INVALID("S020", "Thời gian diễn ra chương trình giảm giá không hợp lệ"),
    DISCOUNT_NOT_EXIST("S021" , "Không tìm thấy chương trình giảm giá nào"),
    ADDRESS_NOT_FOUND("S022", "Không tìm thấy địa chỉ hợp lệ"),
    CoNTENT_FEEDBACk_NULL("S023", "Nội dung phản hồi trống"),
    CANNOT_UPDATE_ANOTHER_FEEDBACL("S024", "Không thể cập nhật phản hồi của người khác"),
    INVALID_CONFIRM_PASS("S025", "Xác nhận mật khẩu không trùng với mật khẩu mới"),
    CONTENT_EMPTY("S026", "Nội dung trống, vui lòng thử lại"),
    NOTIFY_NOT_FOUND("S027", "Không tìm thấy thông báo"),
    EMAIL_EMPTY("S028", "Email trống, vui lòng thử lại"),
    NAME_EMPTY("S029", "Họ và tên trống, vui lòng thử lại"),
    PHONE_EMPTY("S030", "Số điện thoại trống, vui lòng thử lại"),
    ROLE_EMPTY("S031", "Chức vụ trống, vui lòng thử lại")

    ;


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