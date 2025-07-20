package com.planit.planit.common.api.plan.status;

import com.planit.planit.common.api.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum PlanSuccessStatus implements SuccessResponse {

    // 플랜
    PLAN_CREATED(HttpStatus.CREATED, "PLAN2001", "플랜이 생성되었습니다."),
    PLAN_UPDATED(HttpStatus.OK, "PLAN2002", "플랜이 수정되었습니다."),
    PLAN_COMPLETED(HttpStatus.OK, "PLAN2003", "아카이브에 플랜을 저장하였습니다."),
    PLAN_PAUSED(HttpStatus.OK, "PLAN2004", "플랜이 중단되었습니다."),
    PLAN_DELETED(HttpStatus.OK, "PLAN2005", "플랜이 삭제되었습니다."),
    TODAY_PLAN_LIST_FOUND(HttpStatus.OK, "PLAN2006", "오늘의 플랜 목록을 성공적으로 조회하였습니다."),
    PLAN_LIST_FOUND(HttpStatus.OK, "PLAN2007", "플랜 목록을 성공적으로 조회하였습니다."),
    PLAN_FOUND(HttpStatus.OK, "PLAN2008", "플랜을 성공적으로 조회하였습니다."),

    // 아카이브
    ARCHIVE_RESTARTED(HttpStatus.OK, "PLAN2010", "아카이빙된 플랜을 다시 시작하였습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PlanSuccessStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getSuccessStatus() { return httpStatus; }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }
}
