package com.planit.planit.common.api;

import com.planit.planit.common.api.general.GeneralException;
import com.planit.planit.common.api.general.status.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * GeneralException 처리
     * @param exception GeneralException
     * @return ApiResponse - GeneralException
     */
    @ExceptionHandler(GeneralException.class)
    public ApiResponse<ErrorStatus> baseExceptionHandle(GeneralException exception) {
        log.error("Exception has occurred: {}", exception.getMessage());
        return new ApiResponse<>(exception.getStatus());
    }

    /**
     * Exception 처리
     * @param exception Exception
     * @return ApiResponse - INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<ErrorStatus> exceptionHandle(Exception exception) {
        log.error("Exception has occurred: {}", exception.getMessage());
        return new ApiResponse<>(ErrorStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * MethodArgumentTypeMismatchException 처리 - 잘못된 값 입력
     * @param exception MethodArgumentTypeMismatchException
     * @return ApiResponse - BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("Exception has occurred: {}", exception.getMessage());
        String errorMessage = String.format("올바르지 않은 값입니다. %s: %s", exception.getName(), exception.getValue());
        return new ApiResponse<>(ErrorStatus.BAD_REQUEST);
    }
}
