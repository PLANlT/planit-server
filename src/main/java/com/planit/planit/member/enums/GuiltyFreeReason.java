package com.planit.planit.member.enums;

import com.planit.planit.common.api.member.MemberHandler;
import com.planit.planit.common.api.member.status.MemberErrorStatus;

public enum GuiltyFreeReason {
    PHYSICALLY_EXHAUSTED,     // 1. 손에 안 잡혀요
    PLAN_FAILED,              // 2. 계획대로 안 돼요
    NO_TIME_FOR_SCHEDULE,     // 3. 할 일이 많았어요
    LACK_OF_MOTIVATION,       // 4. 너무 지쳐버렸어요
    NONE                      // 5. 초기값

    ;

    private static final String PHYSICALLY_EXHAUSTED_ADVICE = "몸도 마음도 지금은 회복이 필요해요.\n이번 주말엔 일정 비워두고 진짜 휴식을 계획해보는 건 어때요?";
    private static final String PLAN_FAILED_ADVICE = "계획을 수정하는 것도 실행의 일부예요.\n다음 계획은 여유 구간을 조금 더 넣어보는 건 어때요?";
    private static final String NO_TIME_FOR_SCHEDULE_ADVICE = "지금은 ‘회복’이 가장 중요한 계획일지도 몰라요.\n 중요한 일 하나만 먼저 골라서 처리해보는 건 어때요?";
    private static final String LACK_OF_MOTIVATION_ADVICE = "혹시 요즘 나를 너무 몰아세우고 있진 않았나요?\n조금 더 단순하고 쉬운 할 일부터 시작해보는 건 어때요?";

    public static String toAdvice(GuiltyFreeReason reason) {
        return switch (reason) {
            case PHYSICALLY_EXHAUSTED -> PHYSICALLY_EXHAUSTED_ADVICE;
            case PLAN_FAILED -> PLAN_FAILED_ADVICE;
            case NO_TIME_FOR_SCHEDULE -> NO_TIME_FOR_SCHEDULE_ADVICE;
            case LACK_OF_MOTIVATION -> LACK_OF_MOTIVATION_ADVICE;
            default -> throw new MemberHandler(MemberErrorStatus.INVALID_ADVICE_REQUEST);
        };

    }
}
