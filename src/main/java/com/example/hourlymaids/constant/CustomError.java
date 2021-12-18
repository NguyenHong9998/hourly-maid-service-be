package com.example.hourlymaids.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * The type Custom error.
 */
public class CustomError implements Serializable {
    private static final long serialVersionUID = -7079579302701776049L;

    @JsonProperty("field")
    private String field;
    @JsonProperty("code")
    private String code;
    @JsonProperty("params")
    private String[] params;
    @JsonProperty("default_message")
    private String defaultMessage;

    /**
     * Instantiates a new Custom error.
     */
    public CustomError() {
    }

    /**
     * Instantiates a new Custom error.
     *
     * @param code           the code
     * @param params         the params
     * @param field          the field
     * @param defaultMessage the default message
     */
    public CustomError(String code, String[] params, String field, String defaultMessage) {
        this.code = code;
        this.params = params;
        this.field = field;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Instantiates a new Custom error.
     *
     * @param code           the code
     * @param params         the params
     * @param defaultMessage the default message
     */
    public CustomError(String code, String[] params, String defaultMessage) {
        this.code = code;
        this.params = params;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Instantiates a new Custom error.
     *
     * @param field          the field
     * @param code           the code
     * @param defaultMessage the default message
     */
    public CustomError(String field, String code, String defaultMessage) {
        this.field = field;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Instantiates a new Custom error.
     *
     * @param defaultMessage the default message
     */
    public CustomError(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    /**
     * Gets field.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets field.
     *
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
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
     * Sets code.
     *
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Get params string [ ].
     *
     * @return the params
     */
    public String[] getParams() {
        return params;
    }

    /**
     * Sets params.
     *
     * @param params the params to set
     */
    public void setParams(String[] params) {
        this.params = params;
    }

    /**
     * Gets default message.
     *
     * @return the defaultMessage
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Sets default message.
     *
     * @param defaultMessage the defaultMessage to set
     */
    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}

