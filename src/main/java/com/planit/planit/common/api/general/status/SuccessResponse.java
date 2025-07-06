package com.planit.planit.common.api.general.status;

import org.springframework.http.HttpStatus;

public interface SuccessResponse {
    HttpStatus getSuccessStatus();
    String getCode();
    String getMessage();
}
