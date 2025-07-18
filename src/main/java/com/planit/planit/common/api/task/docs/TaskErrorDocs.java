package com.planit.planit.common.api.task.docs;

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
                    name = "TASK_NOT_FOUND",
                    summary = "작업을 찾을 수 없음",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TASK4001",
                      "message": "작업을 찾을 수 없습니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "MEMBER_TASK_NOT_FOUND",
                    summary = "사용자의 작업을 찾을 수 없음",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TASK4002",
                      "message": "사용자의 작업을 찾을 수 없습니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "TASK_ALREADY_COMPLETED",
                    summary = "이미 완료된 작업",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TASK4003",
                      "message": "이미 완료된 작업입니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "TASK_NOT_COMPLETED",
                    summary = "완료되지 않은 작업",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TASK4004",
                      "message": "완료되지 않은 작업입니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "NOT_ROUTINE_OF_TODAY",
                    summary = "오늘의 루틴이 아님",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TASK4005",
                      "message": "오늘의 루틴이 아닙니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "TASK_DELETED",
                    summary = "삭제된 작업",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TASK4006",
                      "message": "삭제된 작업입니다.",
                      "success": false
                    }
                    """
                )
            }
        )
    )
})
public @interface TaskErrorDocs {} 