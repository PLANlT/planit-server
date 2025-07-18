package com.planit.planit.auth.docs;

import com.planit.planit.common.api.general.status.ErrorResponse;
import com.planit.planit.common.api.general.status.SuccessResponse;
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
        responseCode = "200",
        description = "성공",
        content = @Content(
            schema = @Schema(implementation = SuccessResponse.class),
            examples = {
                @ExampleObject(
                    name = "SIGN_IN_SUCCESS",
                    summary = "로그인/회원가입 성공",
                    value = """
                    {
                      "isSuccess": true,
                      "code": "MEMBER2000",
                      "message": "로그인이 완료되었습니다.",
                      "data": {
                        "id": 1,
                        "email": "dbsghwns1209@khu.ac.kr",
                        "name": "‍윤호준[학생](소프트웨어융합대학 컴퓨터공학부)",
                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzduKAjeycpO2YuOykgFvtlZnsg51dKOyGjO2UhO2KuOybqOyWtOycte2VqeuMgO2VmSDsu7Ttk6jthLDqs7XtlZnrtoApIiwicm9sZSI6IlVTRVIiLCJzaWduVXAiOiJ0cnVlIiwiaWF0IjoxNzUyODE3MjY4LCJleHAiOjE3NTI4MjA4Njh9.vI5t9opmiUesBs9xk46X30KTJ96FokMkGk3cdCSQD8U",
                        "refreshToken": null,
                        "signUpCompleted": false,
                        "newMember": true
                      },
                      "success": true
                    }
                    """
                ),
                @ExampleObject(
                    name = "SIGN_OUT_SUCCESS",
                    summary = "로그아웃 성공",
                    value = """
                    {
                      "isSuccess": true,
                      "code": "MEMBER2002",
                      "message": "로그아웃에 성공했습니다.",
                      "success": true
                    }
                    """
                ),
                @ExampleObject(
                    name = "REFRESH_SUCCESS",
                    summary = "토큰 리프레시 성공",
                    value = """
                    {
                      "isSuccess": true,
                      "code": "TOKEN2001",
                      "message": "토큰이 성공적으로 갱신되었습니다.",
                      "success": true,
                      "data": {
                        "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                      }
                    }
                    """
                )
            }
        )
    ),
    @ApiResponse(
        responseCode = "400",
        description = "잘못된 요청",
        content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "INVALID_ID_TOKEN",
                    summary = "유효하지 않은 ID 토큰",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TOKEN4001",
                      "message": "유효하지 않은 ID 토큰입니다.",
                      "success": false
                    }
                    """
                )
            }
        )
    ),
    @ApiResponse(
        responseCode = "401",
        description = "토큰 인증 실패",
        content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = {
                @ExampleObject(
                    name = "INVALID_ACCESS_TOKEN",
                    summary = "유효하지 않은 Access 토큰",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TOKEN4002",
                      "message": "유효하지 않거나 만료된 Access 토큰입니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "INVALID_REFRESH_TOKEN",
                    summary = "유효하지 않은 Refresh 토큰",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TOKEN4003",
                      "message": "유효하지 않거나 변조된 Refresh 토큰입니다.",
                      "success": false
                    }
                    """
                ),
                @ExampleObject(
                    name = "REFRESH_TOKEN_EXPIRED",
                    summary = "Refresh 토큰 만료",
                    value = """
                    {
                      "isSuccess": false,
                      "code": "TOKEN4004",
                      "message": "Refresh 토큰이 만료되었습니다. 다시 로그인해주세요.",
                      "success": false
                    }
                    """
                )
            }
        )
    ),
    @ApiResponse(
        responseCode = "403",
        description = "토큰 인증 실패",
        content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "FORBIDDEN",
                summary = "JWT 인증 실패",
                value = """
                {
                  "isSuccess": false,
                  "code": "FORBIDDEN",
                  "message": "유효하지 않은 토큰입니다.",
                  "success": false
                }
                """
            )
        )
    )
})
public @interface AuthDocs {} 