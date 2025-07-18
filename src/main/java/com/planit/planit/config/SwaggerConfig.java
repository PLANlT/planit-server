package com.planit.planit.config;

import com.planit.planit.common.api.ApiErrorCodeExample;
import com.planit.planit.common.api.ExplainError;
import com.planit.planit.common.api.general.status.ErrorResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI PlanItAPI() {
        Info info = new Info()
                .title("PLANIT API")
                .description("PLANIT API 명세서");

        String jwtSchemeName = "accessToken";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    @Bean
    public OperationCustomizer errorCodeExampleCustomizer() {
        return (operation, handlerMethod) -> {
            ApiErrorCodeExample annotation = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
            if (annotation != null) {
                generateErrorExamples(operation.getResponses(), annotation.value(), annotation.codes());
            }
            return operation;
        };
    }

    private void generateErrorExamples(ApiResponses responses, Class<? extends Enum<?>> enumClass, String[] codes) {
        if (enumClass == null || codes == null) return;
        try {
            Enum<?>[] allConstants = (Enum<?>[]) enumClass.getMethod("values").invoke(null);
            java.util.List<Enum<?>> selected = new java.util.ArrayList<>();
            for (String codeName : codes) {
                for (Enum<?> constant : allConstants) {
                    if (constant.name().equals(codeName)) {
                        selected.add(constant);
                        break;
                    }
                }
            }
            java.util.List<ExampleHolder> holders = selected.stream()
                .filter(e -> e instanceof com.planit.planit.common.api.general.status.ErrorResponse)
                .map(e -> ExampleHolder.of((com.planit.planit.common.api.general.status.ErrorResponse) e))
                .toList();
            java.util.Map<Integer, java.util.List<ExampleHolder>> grouped = holders.stream()
                .collect(java.util.stream.Collectors.groupingBy(ExampleHolder::getStatusCode));
            addExamplesToResponses(responses, grouped);
        } catch (Exception e) {
            // 예외 무시 (문서화 실패시)
        }
    }

    private void addExamplesToResponses(ApiResponses responses, Map<Integer, List<ExampleHolder>> grouped) {
        grouped.forEach((status, examples) -> {
            MediaType mediaType = new MediaType();
            examples.forEach(e -> mediaType.addExamples(e.getName(), e.getExample()));

            Content content = new Content();
            content.addMediaType("application/json", mediaType);

            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setContent(content);

            responses.addApiResponse(String.valueOf(status), apiResponse);
        });
    }

    // 내부 ExampleHolder 클래스 정의
    private static class ExampleHolder {
        private final Example example;
        private final int statusCode;
        private final String name;

        public ExampleHolder(Example example, int statusCode, String name) {
            this.example = example;
            this.statusCode = statusCode;
            this.name = name;
        }

        public static ExampleHolder of(ErrorResponse code) {
            int status = code.getErrorStatus().value();
            String codeStr = code.getCode();
            String message = code.getMessage();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("statusCode", status);
            response.put("errorCode", codeStr);
            response.put("message", message);

            Example example = new Example();
            example.setValue(response);

            // 설명 어노테이션 읽기
            String description = null;
            try {
                // Enum으로 안전하게 캐스팅
                Enum<?> enumConstant = (Enum<?>) code;
                description = code.getClass().getField(enumConstant.name())
                        .getAnnotation(ExplainError.class).value();
            } catch (Exception e) {
                description = message;
            }
            example.setDescription(description);

            return new ExampleHolder(example, status, codeStr);
        }

        public Example getExample() { return example; }
        public int getStatusCode() { return statusCode; }
        public String getName() { return name; }
    }
}