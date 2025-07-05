package com.planit.planit.common.api.member.status;

import com.planit.planit.common.api.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum MemberSuccessStatus implements SuccessResponse {

    // 로그인/회원가입
    SIGN_IN_SUCCESS(HttpStatus.OK, "MEMBER2000", "로그인이 완료되었습니다."),
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "MEMBER2001", "회원가입이 완료되었습니다."),
    SIGN_OUT_SUCCESS(HttpStatus.OK, "MEMBER2002", "로그아웃이 완료되었습니다."),
    TERMS_AGREEMENT_REQUIRED(HttpStatus.OK, "MEMBER2003", "약관 동의가 필요합니다."),

    // 길티프리
    GUILTY_FREE_SET(HttpStatus.OK, "MEMBER2010", "길티프리를 활성화하였습니다."),
    GUILTY_FREE_FOUND(HttpStatus.OK, "MEMBER2011", "길티프리 활성일을 조회하였습니다."),
    GUILTY_FREE_REASON_LIST_FOUND(HttpStatus.OK, "MEMBER2012", "길티프리 사유 목록을 조회하였습니다."),

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
