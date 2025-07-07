package com.planit.planit.common.api.general.status;

import org.springframework.http.HttpStatus;

public enum SuccessStatus implements SuccessResponse {

    OK(HttpStatus.OK,"COMMON2000", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON2001", "새 리소스가 생성되었습니다."),
    REFRESH_SUCCESS(HttpStatus.OK, "COMMON2002", "리프레시 토큰이 성공적으로 갱신되었습니다.")


    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    SuccessStatus(HttpStatus httpStatus, String code, String message) {
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
