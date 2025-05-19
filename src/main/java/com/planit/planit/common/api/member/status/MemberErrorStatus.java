package com.planit.planit.common.api.member.status;

import com.planit.planit.common.api.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum MemberErrorStatus implements ErrorResponse {

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    MemberErrorStatus(HttpStatus httpStatus, String code, String message) {
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
