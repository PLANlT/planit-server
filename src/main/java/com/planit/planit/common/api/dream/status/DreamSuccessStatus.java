package com.planit.planit.common.api.dream.status;

import com.planit.planit.common.api.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum DreamSuccessStatus implements SuccessResponse {

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    DreamSuccessStatus(HttpStatus httpStatus, String code, String message) {
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
