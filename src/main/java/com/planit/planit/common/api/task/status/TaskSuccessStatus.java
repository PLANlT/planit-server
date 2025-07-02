package com.planit.planit.common.api.task.status;

import com.planit.planit.common.api.general.status.SuccessResponse;
import org.springframework.http.HttpStatus;

public enum TaskSuccessStatus implements SuccessResponse {

    TASK_CREATED(HttpStatus.CREATED, "TASK2001", "작업을 생성하였습니다."),
    TASK_TITLE_UPDATED(HttpStatus.OK, "TASK2002", "작업명을 수정하였습니다."),
    TASK_ROUTINE_SET(HttpStatus.OK, "TASK2003", "작업 루틴을 설정하였습니다."),
    TASK_DELETED(HttpStatus.OK, "TASK2004", "작업을 삭제하였습니다."),
    TASK_COMPLETED(HttpStatus.CREATED, "TASK2005", "작업을 완료하였습니다."),
    TASK_COMPLETION_CANCELED(HttpStatus.OK, "TASK2006", "작업 완료를 취소하였습니다."),
    TASK_ROUTINE_FOUND(HttpStatus.OK, "TASK2007", "작업 루틴을 조회하였습니다."),


    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    TaskSuccessStatus(HttpStatus httpStatus, String code, String message) {
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
