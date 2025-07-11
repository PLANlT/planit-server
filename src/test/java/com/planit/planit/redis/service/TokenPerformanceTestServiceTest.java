package com.planit.planit.redis.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.planit.planit.common.aop.LogAspect;
import com.planit.planit.common.aop.handler.ExecutionTimeHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TokenPerformanceTestServiceTest {

    private TokenPerformanceTestService proxyService;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        // 1. 로그 가로채기 세팅
        Logger logger = (Logger) LoggerFactory.getLogger("com.planit.planit.common.aop.handler.ExecutionTimeHandler");
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        // 2. RedisTemplate과 ValueOperations mock
        RedisTemplate<String, Object> redisTemplate = Mockito.mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("token:test")).thenReturn("mock-value");
        when(redisTemplate.keys("*")).thenReturn(Set.of("token:test", "token:another"));
        when(valueOperations.get("token:test")).thenReturn("mock-value");
        when(valueOperations.get("token:another")).thenReturn("another-value");

        // 3. 실제 대상 객체 (원본 서비스)
        TokenPerformanceTestService target = new TokenPerformanceTestService(redisTemplate);

        // 4. 핸들러 및 공통 AOP 등록
        ExecutionTimeHandler handler = new ExecutionTimeHandler();
        LogAspect logAspect = new LogAspect(List.of(handler));

        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(logAspect);

        // 5. AOP가 적용된 프록시 객체 생성
        proxyService = factory.getProxy();
    }

    @Test
    @DisplayName("fetchWithKey 메서드 AOP 로그 테스트")
    void fetchWithKey_logsExecutionTime() {
        // when
        proxyService.fetchWithKey("test");

        // then
        boolean logged = listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("[ExecutionTime]"));

        assertThat(logged).isTrue();
    }

    @Test
    @DisplayName("fetchWithLinearSearch 메서드 AOP 로그 테스트")
    void fetchWithLinearSearch_logsExecutionTime() {
        // when
        proxyService.fetchWithLinearSearch("mock-value");

        // then
        boolean logged = listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("[ExecutionTime]"));

        assertThat(logged).isTrue();
    }
}
