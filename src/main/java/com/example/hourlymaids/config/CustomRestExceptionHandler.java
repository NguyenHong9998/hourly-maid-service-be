package com.example.hourlymaids.config;


import com.example.hourlymaids.constant.CustomError;
import com.example.hourlymaids.constant.CustomException;
import com.example.hourlymaids.constant.Error;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Custom rest exception handler.
 */
@RestControllerAdvice
@RestController
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error(ex.getMessage(), ex.fillInStackTrace());
        List<CustomError> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(
                    new CustomError(error.getField(), Error.REQUIRED_FIELD.getCode(), error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(new CustomError(error.getObjectName(), Error.REQUIRED_FIELD.getCode(),
                    error.getDefaultMessage()));
        }
        ResponseDataAPI res = new ResponseDataAPI();
        res.setSuccess(false);
        res.setErrors(errors);
        return handleExceptionInternal(ex, res, headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<CustomError> errors = new ArrayList<>();
        logger.error(ex.getMessage(), ex.fillInStackTrace());
        errors.add(
                new CustomError(ex.getParameterName(), Error.REQUIRED_FIELD.getCode(), " parameter is missing"));
        ResponseDataAPI res = new ResponseDataAPI();
        res.setSuccess(false);
        res.setErrors(errors);
        return new ResponseEntity<>(res, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle bad request exception response entity.
     *
     * @param exception  the exception
     * @param webRequest the web request
     * @return the response entity
     */
    @ExceptionHandler({ BadRequestException.class })
    public ResponseEntity<ResponseDataAPI> handleBadRequestException(BadRequestException exception,
                                                                     WebRequest webRequest) {
        logger.error(exception.getMessage(), exception.fillInStackTrace());
        return ResponseEntity.badRequest().body(ResponseDataAPI.builder()
                .success(false)
                .error(new ArrayList<>(exception.getErrors()))
                .build());
    }

    @ExceptionHandler({ CustomException.class })
    public ResponseEntity<Object> handleCustomException(Exception e, WebRequest request) {
        logger.error(e.getMessage(), e.fillInStackTrace());
        CustomException customException = (CustomException) e;
        CustomError customError = null;

        if (customException.getParams() != null) {
            customError = new CustomError(customException.getCode(),
                    customException.getParams(), customException.getMessage());
        } else {
            customError = new CustomError("", customException.getCode(), customException.getMessage());
        }

        return ResponseEntity.status(customException.getHttpStatus())
                .body(ResponseDataAPI.builder().success(false)
                        .error(Collections.singletonList(customError))
                        .build());
    }
}