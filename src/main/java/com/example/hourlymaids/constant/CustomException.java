package com.example.hourlymaids.constant;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

/**
 * The type Custom exception.
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = -3163757886763387095L;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;
    private String[] params;

    /**
     * Instantiates a new Custom exception.
     *
     * @param message    the message
     * @param code       the code
     * @param httpStatus the http status
     */
    public CustomException(String message, String code, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public CustomException(String message, String code, HttpStatus httpStatus, List<String> params) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = code;
        if (!CollectionUtils.isEmpty(params)) {
            this.params = new String[params.size()];
            params.toArray(this.params);
        }
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Gets http status.
     *
     * @return the http status
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the params
     */
    public String[] getParams() {
        return params;
    }

}
