package com.planit.planit.common.api.task.status;

import com.planit.planit.common.api.ExplainError;
import com.planit.planit.common.api.general.status.ErrorResponse;
import org.springframework.http.HttpStatus;

public enum TaskErrorStatus implements ErrorResponse {

    @ExplainError("작업을 찾을 수 없음")
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK4001", "작업을 찾을 수 없습니다."),
    @ExplainError("사용자의 작업을 찾을 수 없음")
    MEMBER_TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK4002", "사용자의 작업을 찾을 수 없습니다."),
    @ExplainError("이미 완료된 작업")
    TASK_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "TASK4003", "이미 완료된 작업입니다."),
    @ExplainError("완료되지 않은 작업")
    TASK_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "TASK4004", "완료되지 않은 작업입니다."),
    @ExplainError("오늘의 루틴이 아님")
    NOT_ROUTINE_OF_TODAY(HttpStatus.BAD_REQUEST, "TASK4005", "오늘의 루틴이 아닙니다."),
    @ExplainError("삭제된 작업")
    TASK_DELETED(HttpStatus.BAD_REQUEST, "TASK4006", "삭제된 작업입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TaskErrorStatus(HttpStatus httpStatus, String code, String message) {
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
