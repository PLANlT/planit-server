package com.planit.planit.common.api.general.status;

import org.springframework.http.HttpStatus;

public interface ErrorResponse {
    HttpStatus getErrorStatus();
    String getCode();
    String getMessage();
}
