package com.planit.planit.common.api.plan.docs;

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
            examples = {
                @ExampleObject(
                    name = "PLAN_NOT_FOUND",
                    summary = "플랜을 찾을 수 없음",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "PLAN4001",
                      "message": "플랜을 찾을 수 없습니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "MEMBER_PLAN_NOT_FOUND",
                    summary = "사용자의 플랜을 찾을 수 없음",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "PLAN4002",
                      "message": "사용자의 플랜을 찾을 수 없습니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "PLAN_DELETED",
                    summary = "삭제된 플랜",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "PLAN4003",
                      "message": "삭제된 플랜입니다.",
                      "success": false
                    }
                    """
                )
            }
        )
    )
})
public @interface PlanErrorDocs {} 