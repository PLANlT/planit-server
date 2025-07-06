package com.planit.planit.auth.service;

import org.junit.jupiter.api.DisplayName; // DisplayName import 추가
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthServiceImplTest {

//    private AuthServiceImpl authServiceImpl;



    @Test
    @Order(1)
    @DisplayName("로그인 시 아이디(이메일)가 null이면 오류를 반환한다") // DisplayName 추가
    public void 아이디가_null이면_오류반환(){

        //given

        //when

        //then
    }

    @Test
    @Order(2)
    @DisplayName("로그인 시 비밀번호가 null이면 오류를 반환한다") // DisplayName 추가
    public void 비밀번호_null이면_오류반환(){

        //given

        //when

        //then
    }

    @Test
    @Order(3)
    @DisplayName("로그인 시 아이디(이메일) 형식이 유효하지 않으면 오류를 반환한다") // DisplayName 추가
    public void 아이디_형식_이상하면_오류(){

        //given

        //when

        //then
    }

    @Test
    @Order(4)
    @DisplayName("로그인 시 비밀번호 형식이 유효하지 않으면 오류를 반환한다") // DisplayName 추가
    public void 비밀번호_형식_이상하면_오류(){

        //given

        //when

        //then
    }

    @Test
    @Order(5)
    @DisplayName("탈퇴한 계정으로 로그인 시도 시 오류를 반환한다") // DisplayName 추가
    public void 탈퇴한_계정_로그인시_오류(){

        //given

        //when

        //then
    }

    @Test
    @Order(6)
    @DisplayName("존재하지 않는 아이디(이메일)로 로그인 시 오류를 반환한다") // DisplayName 추가
    public void 아이디가_없으면_오류(){

        //given

        //when

        //then
    }

    @Test
    @Order(7)
    @DisplayName("아이디는 존재하지만 비밀번호가 비어있으면 오류를 반환한다") // DisplayName 추가
    public void 아이디있고_비밀번호없으면_오류(){

        //given

        //when

        //then
        }

    @Test
    @Order(8)
    @DisplayName("아이디는 존재하지만 비밀번호가 일치하지 않으면 오류를 반환한다") // DisplayName 추가
    public void 아이디있고_비밀번호틀리면_오류(){

        //given

            //when

            //then
        }

    @Test
    @Order(9)
    @DisplayName("다 맞을 때 로그인 성공")
    public void 정상_로그인(){

                //given

                //when

                //then

    }

    // --- 회원가입 테스트 케이스 ---

    @Test
    @Order(10) // 로그인 테스트 이후 순서 부여
    @DisplayName("성공적인 회원가입 시 새로운 회원을 생성하고 성공 응답을 반환한다")
    public void 회원가입_성공() {
        // Given (Mock 설정: 이메일 중복 없음, save 성공)
        // When (authService.register() 호출)
        // Then (예상 결과 검증: 반환값, save 호출 여부 등)
    }

    @Test
    @Order(11)
    @DisplayName("회원가입 시 이미 존재하는 이메일이면 오류를 반환한다")
    public void 회원가입_실패_중복_이메일() {
        // Given (Mock 설정: findByEmail 호출 시 Optional.of(기존_회원) 반환)
        // When (authService.register() 호출)
        // Then (예상 예외 검증: DUPLICATE_EMAIL 오류 등)
    }

    @Test
    @Order(12)
    @DisplayName("회원가입 시 이메일이 null이면 오류를 반환한다")
    public void 회원가입_실패_이메일_null() {
        // Given (requestDto에 email = null 설정)
        // When (authService.register() 호출)
        // Then (예상 예외 검증: Input Validation 오류 등)
    }

    @Test
    @Order(13)
    @DisplayName("회원가입 시 이메일 형식이 유효하지 않으면 오류를 반환한다")
    public void 회원가입_실패_이메일_형식_오류() {
        // Given (requestDto에 유효하지 않은 이메일 형식 설정)
        // When (authService.register() 호출)
        // Then (예상 예외 검증: Input Validation 오류 등)
    }

    @Test
    @Order(14)
    @DisplayName("회원가입 시 비밀번호가 null이면 오류를 반환한다")
    public void 회원가입_실패_비밀번호_null() {
        // Given (requestDto에 password = null 설정)
        // When (authService.register() 호출)
        // Then (예상 예외 검증: Input Validation 오류 등)
    }

    @Test
    @Order(15)
    @DisplayName("회원가입 시 비밀번호 형식이 유효하지 않으면 오류를 반환한다")
    public void 회원가입_실패_비밀번호_형식_오류() {
        // Given (requestDto에 유효하지 않은 비밀번호 형식 설정: 짧거나, 특정 문자 미포함 등)
        // When (authService.register() 호출)
        // Then (예상 예외 검증: Input Validation 오류 등)
    }

    @Test
    @Order(16)
    @DisplayName("회원가입 시 사용자 이름이 null이거나 비어있으면 오류를 반환한다")
    public void 회원가입_실패_사용자_이름_누락() {
        // Given (requestDto에 memberName = null 또는 "" 설정)
        // When (authService.register() 호출)
        // Then (예상 예외 검증: Input Validation 오류 등)
    }

}
