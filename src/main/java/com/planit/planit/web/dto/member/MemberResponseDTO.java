package com.planit.planit.web.dto.member;

import com.planit.planit.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
public class MemberResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ConsecutiveDaysDTO {

        private final Long currentConsecutiveDays;
        private final Long maxConsecutiveDays;
        private final Long perfectConsecutiveDays;

        public static ConsecutiveDaysDTO of(Member member) {

            LocalDate today = LocalDate.now();
            LocalDate lastAttendanceDate = member.getLastAttendanceDate();

            // 오늘 출석한 경우
            if (lastAttendanceDate.equals(today)) {
                return new ConsecutiveDaysDTO(
                        ChronoUnit.DAYS.between(member.getAttendanceStartedAt(), today) + 1L,       // 현재 연속일
                        member.getMaxConsecutiveDays(),                                                                 // 최대 연속일
                        member.getAttendanceStartedAt().isAfter(member.getLastGuiltyFreeDate()) ?                       // 완벽 연속일
                                ChronoUnit.DAYS.between(member.getAttendanceStartedAt(), today) + 1L :
                                ChronoUnit.DAYS.between(member.getLastGuiltyFreeDate(), today) + 1L

                );
            } else {
                // 오늘 출석하면 연속 출석인 경우
                if (member.isConsecutiveAttendance(today)) {
                    return new ConsecutiveDaysDTO(
                            member.getLastGuiltyFreeDate().isAfter(member.getLastAttendanceDate()) ?                    // 현재 연속일
                                    ChronoUnit.DAYS.between(member.getAttendanceStartedAt(), member.getLastGuiltyFreeDate()) + 1L :
                                    ChronoUnit.DAYS.between(member.getAttendanceStartedAt(), member.getLastAttendanceDate()) + 1L,
                            member.getMaxConsecutiveDays(),                                                             // 최대 연속일
                            member.getAttendanceStartedAt().isAfter(member.getLastGuiltyFreeDate()) ?                   // 완벽 연속일
                                    ChronoUnit.DAYS.between(member.getAttendanceStartedAt(), member.getLastAttendanceDate()) + 1L :
                                    ChronoUnit.DAYS.between(member.getLastGuiltyFreeDate(), member.getLastAttendanceDate()) + 1L
                    );
                }
                // 연속 출석이 아닌 경우
                return new ConsecutiveDaysDTO(0L, member.getMaxConsecutiveDays(), 0L);
            }

        }
    }
}
