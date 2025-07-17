package com.planit.planit.common.api.token.status;

import com.planit.planit.common.api.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum TokenErrorStatus implements ErrorResponse {

    INVALID_ID_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN4001", "유효하지 않은 ID 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4002", "유효하지 않거나 만료된 Access 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN4003", "유효하지 않거나 변조된 Refresh 토큰입니다."),
        REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN4004", "Refresh 토큰이 만료되었습니다. 다시 로그인해주세요."),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TokenErrorStatus(HttpStatus httpStatus, String code, String message) {
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
