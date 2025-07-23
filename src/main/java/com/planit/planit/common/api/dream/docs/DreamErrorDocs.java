package com.planit.planit.common.api.dream.docs;

import com.planit.planit.common.api.general.status.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation
@ApiResponses({
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "BAD_REQUEST",
                summary = "잘못된 요청",
                value = """
                {
                  "isSuccess": false,
                  "code": "BAD_REQUEST",
                  "message": "잘못된 형식의 요청입니다.",
                  "success": false
                }
                """
            )
        )
    )
})
public @interface DreamErrorDocs {} 