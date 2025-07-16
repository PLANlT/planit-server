package com.planit.planit.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.planit.planit.common.api.general.status.ErrorResponse;
import com.planit.planit.common.api.general.status.ErrorStatus;
import com.planit.planit.common.api.general.status.SuccessResponse;
import com.planit.planit.common.api.general.status.SuccessStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T>  {

    @JsonProperty("isSuccess")
    private final boolean isSuccess;            // 성공 여부
    private final String code;                  // 응답 코드
    private final String message;               // 응답 메시지

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;                             // 응답 데이터

    public ApiResponse(boolean isSuccess, String code, String message, T data) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Success
    public static ApiResponse<Void> onSuccess(SuccessResponse status) {
        return new ApiResponse<>(status);
    }

    // Success with Data
    public static <T> ApiResponse<T> onSuccess(SuccessResponse status, T data) {
        return new ApiResponse<>(true, status.getCode(), status.getMessage(), data);

    }

    // Failure
    public static <T> ApiResponse<T> onFailure(ErrorResponse status) {
        return new ApiResponse<>(status);
    }

    // Failure with Data
    public static <T> ApiResponse<T> onFailure(ErrorResponse status, T data) {
        return new ApiResponse<>(false, status.getCode(), status.getMessage(), data);
    }

    private ApiResponse(SuccessResponse response) {
        this.isSuccess = true;
        this.code = response.getCode();
        this.message = response.getMessage();
    }

    private ApiResponse(ErrorResponse response) {
        this.isSuccess = false;
        this.code = response.getCode();
        this.message = response.getMessage();
    }

}
