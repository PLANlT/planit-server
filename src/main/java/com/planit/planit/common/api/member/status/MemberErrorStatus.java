package com.planit.planit.common.api.member.status;

import com.planit.planit.common.api.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum MemberErrorStatus implements ErrorResponse {

    // 회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "회원을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER4002", "이미 가입된 회원입니다."),
    MEMBER_ALREADY_SIGN_UP_COMPLETED(HttpStatus.BAD_REQUEST, "MEMBER4003", "약관 동의가 완료된 회원입니다."),

    // 길티프리
    GUILTY_FREE_ACTIVATION_FORBIDDEN(HttpStatus.BAD_REQUEST, "MEMBER4010", "길티프리는 주 1회만 활성화할 수 있습니다."),
    INVALID_ADVICE_REQUEST(HttpStatus.BAD_REQUEST, "MEMBER4011", "길티프리 조언을 요청할 수 없습니다."),
    LAST_GUILTY_FREE_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4012", "최근 길티프리 정보를 조회할 수 없습니다."),
    GUILTY_FREE_NOT_ACTIVATED(HttpStatus.NOT_FOUND, "MEMBER4013", "길티프리 활성 내역이 없습니다."),

    DIFFERENT_SIGN_TYPE(HttpStatus.BAD_REQUEST, "MEMBER4020", "이미 다른 소셜 계정으로 가입된 이메일입니다."),

    //알림 설정
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4030", "알림 설정이 존재하지 않습니다.")
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
