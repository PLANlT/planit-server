package com.planit.planit.common.api.member.status;

import com.planit.planit.common.api.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum MemberSuccessStatus implements SuccessResponse {

    // 길티프리
    GUILTY_FREE_SET(HttpStatus.OK, "MEMBER2001", "길티프리를 활성화하였습니다."),
    GUILTY_FREE_FOUND(HttpStatus.OK, "MEMBER2002", "길티프리 활성일을 조회하였습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemberSuccessStatus(HttpStatus httpStatus, String code, String message) {
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
