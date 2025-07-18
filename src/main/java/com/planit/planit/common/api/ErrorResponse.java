package com.planit.planit.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "에러 응답 형식")
public class ErrorResponse {

    @Schema(description = "에러 상태 코드", example = "400")
    private int statusCode;

    @Schema(description = "에러 코드 (Enum name)", example = "BAD_REQUEST")
    private String errorCode;

    @Schema(description = "에러 메시지", example = "잘못된 요청입니다.")
    private String message;
}
