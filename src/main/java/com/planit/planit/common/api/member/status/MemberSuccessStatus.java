package com.planit.planit.common.api.member.status;

import com.planit.planit.common.api.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum MemberSuccessStatus implements SuccessResponse {

    // 로그인/회원가입
    SIGN_IN_SUCCESS(HttpStatus.OK, "MEMBER2000", "로그인이 완료되었습니다."),
    SIGN_UP_SUCCESS(HttpStatus.CREATED, "MEMBER2001", "회원가입이 완료되었습니다."),
    SIGN_OUT_SUCCESS(HttpStatus.OK, "MEMBER2002", "로그아웃이 완료되었습니다."),
    TERMS_AGREEMENT_REQUIRED(HttpStatus.OK, "MEMBER2003", "약관 동의가 필요합니다."),
    TERM_AGREEMENT_COMPLETED(HttpStatus.OK,"MEMBER2005" , "약관 동의가 완료되었습니다." ),
    TERMS_URLS_FOUND(HttpStatus.FOUND,"MEMBER2006", "약관 버전이 조회되었습니다." ),
    // 길티프리
    GUILTY_FREE_SET(HttpStatus.OK, "MEMBER2010", "길티프리를 활성화하였습니다."),
    GUILTY_FREE_FOUND(HttpStatus.OK, "MEMBER2011", "길티프리 활성일을 조회하였습니다."),
    GUILTY_FREE_REASON_LIST_FOUND(HttpStatus.OK, "MEMBER2012", "길티프리 사유 목록을 조회하였습니다."),

    // 연속일
    CONSECUTIVE_DAYS_FOUND(HttpStatus.OK, "MEMBER2004", "연속일을 조회하였습니다."),

    //회원 정보 관련
    NOTIFICATION_SETTING_FETCHED(HttpStatus.OK, "MEMBER2020", "알림 설정 정보를 조회하였습니다."),
    NOTIFICATION_DAILY_TASK_TOGGLED(HttpStatus.OK, "MEMBER2021", "오늘의 할 일 알림 설정을 변경하였습니다."),
    NOTIFICATION_GUILTY_FREE_TOGGLED(HttpStatus.OK, "MEMBER2022", "길티프리 모드 알림 설정을 변경하였습니다."),
    MEMBER_INFO_FETCHED(HttpStatus.OK,"MEMBER2023", "회원 정보가 성공적으로 조회되었습니다." );




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
