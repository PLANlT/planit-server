package com.planit.planit.common.api.task.status;

import com.planit.planit.common.api.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum TaskErrorStatus implements ErrorResponse {

    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK4001", "작업을 찾을 수 없습니다."),
    MEMBER_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK4002", "사용자의 작업을 찾을 수 없습니다."),



    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TaskErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getErrorStatus() { return httpStatus; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
