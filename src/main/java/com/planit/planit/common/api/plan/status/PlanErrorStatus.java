package com.planit.planit.common.api.plan.status;

import com.planit.planit.common.api.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum PlanErrorStatus implements ErrorResponse {

    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN4001", "플랜을 찾을 수 없습니다."),
    MEMBER_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN4002", "사용자의 플랜을 찾을 수 없습니다."),
    PLAN_DELETED(HttpStatus.NOT_FOUND, "PLAN4003", "삭제된 플랜입니다."),
    PLAN_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "PLAN4004", "이미 삭제된 플랜을 다시 삭제할 수 없습니다."),
    PLAN_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST, "PLAN4005", "진행중인 플랜이 아닙니다."),
    PLAN_NOT_ARCHIVED(HttpStatus.BAD_REQUEST, "PLAN4006", "아카이빙된 플랜이 아닙니다."),
    INVALID_PLAN_STATUS(HttpStatus.BAD_REQUEST, "PLAN4007", "잘못된 플랜 진행 여부입니다."),

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
