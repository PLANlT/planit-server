package com.planit.planit.common.api;

import java.lang.annotation.*;

@Repeatable(ApiErrorCodeExamples.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodeExample {
    Class<? extends Enum<?>> value();
    String[] codes();
} 