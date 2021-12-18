package com.example.hourlymaids.config;

import com.example.hourlymaids.constant.CustomError;
import com.example.hourlymaids.constant.Error;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Bad request exception.
 */
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 6614468782904595822L;
    private List<CustomError> errors;

    /**
     * Instantiates a new Bad request exception.
     */
    private BadRequestException() {
        // No-op
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Instantiates a new Bad request exception.
     *
     * @param errors the errors
     */
    private BadRequestException(List<CustomError> errors) {
        this.errors = errors;
    }

    /**
     * Gets error.
     *
     * @return the error
     */
    public List<CustomError> getErrors() {
        return errors;
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        /**
         * Instantiates a new Builder.
         */
        private Builder() {
            errors = new ArrayList<>();
        }

        private List<CustomError> errors;

        /**
         * Add error.
         *
         * @param error the error
         * @return the builder
         */
        public Builder addError(Error error) {
            errors.add(new CustomError("", error.getCode(), error.getMessage()));
            return this;
        }

        /**
         * Add error.
         *
         * @param error the error
         * @return the builder
         */
        public Builder addError(List<CustomError> error) {
            errors.addAll(error);
            return this;
        }

        /**
         * Add error.
         *
         * @param field the field
         * @param error the error
         * @return the builder
         */
        public Builder addError(String field, Error error) {
            errors.add(new CustomError(field, error.getCode(), error.getMessage()));
            return this;
        }

        /**
         * Add error.
         *
         * @param error the error
         * @return the builder
         */
        public Builder addError(CustomError error) {
            errors.add(error);
            return this;
        }

        /**
         * Add error.
         *
         * @param error the error
         * @return the builder
         */
        public Builder addError(String error) {
            errors.add(new CustomError(error));
            return this;
        }

        /**
         * Add error builder.
         *
         * @param errors the errors
         * @return the builder
         */
        public Builder addError(Errors errors) {
            this.errors.addAll(errors.getAllErrors().stream()
                    .map(error -> new CustomError(error.getCode(), error.getArguments() == null ?
                            null :
                            Arrays.stream(error.getArguments()).map(object -> object == null ? null : object.toString().trim())
                                    .collect(Collectors.toList()).toArray(new String[]{}),
                            (error instanceof FieldError) ? ((FieldError) error).getField() : null,
                            error.getDefaultMessage()))
                    .collect(Collectors.toList()));
            return this;
        }

        /**
         * Build bad request exception.
         *
         * @return the bad request exception
         */
        public BadRequestException build() {
            return new BadRequestException(errors);
        }
    }
}