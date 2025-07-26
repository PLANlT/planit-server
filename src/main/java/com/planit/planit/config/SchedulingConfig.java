package com.planit.planit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * 스케줄러 설정
 * - 스케줄러가 사용할 스레드 풀 설정
 * - 기본적으로 단일 스레드로 실행되지만, 여러 스케줄러가 동시에 실행될 수 있도록 스레드 풀 설정
 */
@Configuration
public class SchedulingConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 스케줄러용 스레드 풀 설정 (기본값: 1개 스레드)
        // 여러 스케줄러가 동시에 실행될 수 있도록 스레드 풀 크기를 늘림
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(5));
    }
} 