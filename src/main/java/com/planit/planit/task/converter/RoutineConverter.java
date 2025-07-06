package com.planit.planit.task.converter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class RoutineConverter {

    /**
     * Byte로 저장된 루틴을 List<DayOfWeek>로 변환
     * @param routineByte : Byte 루틴
     * @return DayOfWeek 리스트
     * MON(1) : 0000 0001
     * TUE(2) : 0000 0010
     * ...
     * SUN(7) : 0100 0000
     */
    public static List<DayOfWeek> byteToRoutineDays(Byte routineByte)  {
        List<DayOfWeek> routineDays = new ArrayList<>();

        for (int i=0; i<7; i++) {
            // routineDay를 i씩 rightShift 하여 1(0b00000001)과 and 연산
            int bit = (routineByte >> i) & 1;

            // 만약 bit 값이 1이면 해당 요일에 루틴 존재
            if (bit == 1) {
                int dayValue = (i % 7) + 1;
                routineDays.add(DayOfWeek.of(dayValue));
            }
        }
        return routineDays;
    }

    /**
     * List<DayOfWeek>를 Byte로 변환
     * @param routineDays : DayOfWeek 리스트
     * @return Byte 루틴
     * MON(1) : 0000 0001
     * TUE(2) : 0000 0010
     * ...
     * SUN(7) : 0100 0000
     */
    public static Byte routineDaysToByte(List<DayOfWeek> routineDays) {
        int routineByte = 0;

        for (DayOfWeek day : routineDays) {
            int bit = 1;
            int dayValue = day.getValue();
            routineByte = routineByte | (bit << (dayValue - 1));
        }

        return (byte) routineByte;
    }
}
