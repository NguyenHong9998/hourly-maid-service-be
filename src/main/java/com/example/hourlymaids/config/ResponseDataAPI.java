package com.example.hourlymaids.config;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

/**
 * The type Response data api.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResponseDataAPI implements Serializable {
    private static final long serialVersionUID = -996509413862521394L;
    private Object data;
    @JsonProperty("total_rows")
    private Object totalRows;
    @JsonAlias({"errors", "error"})
    private Object errors;
    private String message;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("limit")
    private Integer limit;
    private Boolean success = true;

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Instantiates a new Response data api.
     */
    public ResponseDataAPI() {
    }

    /**
     * Instantiates a new Response data api.
     *
     * @param data      the data
     * @param totalRows the total rows
     * @param errors    the errors
     * @param message   the message
     * @param offset    the offset
     * @param limit     the limit
     * @param success   the success
     */
    ResponseDataAPI(Object data, Object totalRows, Object errors, String message, Integer offset, Integer limit,
                    Boolean success) {
        super();
        this.data = data;
        this.totalRows = totalRows;
        this.errors = errors;
        this.message = message;
        this.offset = offset;
        this.limit = limit;
        this.success = success;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Gets total rows.
     *
     * @return the total rows
     */
    public Object getTotalRows() {
        return totalRows;
    }

    /**
     * Sets total rows.
     *
     * @param totalRows the total rows
     */
    public void setTotalRows(Object totalRows) {
        this.totalRows = totalRows;
    }

    /**
     * Gets errors.
     *
     * @return the errors
     */
    public Object getErrors() {
        return errors;
    }

    /**
     * Sets errors.
     *
     * @param errors the errors
     */
    public void setErrors(Object errors) {
        this.errors = errors;
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

    /**
     * Gets offset.
     *
     * @return the offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * Sets offset.
     *
     * @param offset the offset
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * Gets limit.
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets limit.
     *
     * @param limit the limit
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Gets success.
     *
     * @return the success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * Sets success.
     *
     * @param success the success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * This class is builder pattern for ResponseDataApi
     */
    public static class Builder {
        private Object data;
        private Object totalRows;
        private Object error;
        private String message;
        private Integer offset;
        private Integer limit;
        private Boolean success = true;

        /**
         * Data builder.
         *
         * @param data the data
         * @return the builder
         */
        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        /**
         * Total rows builder.
         *
         * @param totalRows the total rows
         * @return the builder
         */
        public Builder totalRows(Object totalRows) {
            this.totalRows = totalRows;
            return this;
        }

        /**
         * Error builder.
         *
         * @param error the error
         * @return the builder
         */
        public Builder error(Object error) {
            this.error = error;
            return this;
        }

        /**
         * Message builder.
         *
         * @param message the message
         * @return the builder
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Offset builder.
         *
         * @param offset the offset
         * @return the builder
         */
        public Builder offset(Integer offset) {
            this.offset = offset;
            return this;
        }

        /**
         * Limit builder.
         *
         * @param limit the limit
         * @return the builder
         */
        public Builder limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Success builder.
         *
         * @param success the success
         * @return the builder
         */
        public Builder success(Boolean success) {
            this.success = success;
            return this;
        }

        /**
         * Build response data api.
         *
         * @return the response data api
         */
        public ResponseDataAPI build() {
            return new ResponseDataAPI(data, totalRows, error, message, offset, limit, success);
        }
    }
}