package com.planit.planit.common.api.plan.status;

import com.planit.planit.common.api.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum PlanErrorStatus implements ErrorResponse {

    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN4001", "플랜을 찾을 수 없습니다."),
    MEMBER_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN4002", "사용자의 플랜을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PlanErrorStatus(HttpStatus httpStatus, String code, String message) {
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
