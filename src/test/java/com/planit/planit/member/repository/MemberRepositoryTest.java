package com.planit.planit.member.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.Role;
import com.planit.planit.member.enums.SignType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // JPA 관련 컴포넌트만 로드하여 테스트
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 내장 DB 사용 (테스트용)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Order(1)
    @DisplayName("새로운 멤버 저장 가능-성공")
    @Transactional
    public void testSaveMember(){
        //given
        Member newMember = Member.builder()
                .email("test@example.com")
                .password("password")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("테스터")
                .role(Role.USER)
                .build();

        //when
        Member savedMember = memberRepository.save(newMember);

        //then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test@example.com");
        assertThat(savedMember.getMemberName()).isEqualTo("테스터");
        assertThat(savedMember.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @Order(2)
    @DisplayName("이메일로 멤버를 조회할 수 있다-성공")
    @Transactional
    void testFindByEmail_ExistingMember() {
        // Given
        Member existingMember = Member.builder()
                .email("existing@example.com")
                .password("password")
                .signType(SignType.GOOGLE)
                .guiltyFreeMode(false)
                .memberName("기존회원")
                .role(Role.USER)
                .build();
        memberRepository.save(existingMember);

        // When
        Optional<Member> foundMember = memberRepository.findByEmail("existing@example.com");

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo("existing@example.com");
        assertThat(foundMember.get().getMemberName()).isEqualTo("기존회원");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회 시 비어있는 Optional을 반환한다-성공")
    @Order(3)
    @Transactional
    void testFindByEmail_NonExistingMember() {
        // Given

        // When
        Optional<Member> foundMember = memberRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundMember).isEmpty();
    }

    @Test
    @DisplayName("중복된 이메일로 저장 시 예외가 발생한다-성공")
    @Order(4)
    @Transactional
    void testSaveDuplicateEmailThrowsException() {
        // Given
        Member member1 = Member.builder()
                .email("duplicate@example.com")
                .password("password")
                .signType(SignType.KAKAO)
                .guiltyFreeMode(false)
                .memberName("중복1")
                .role(Role.USER)
                .build();
        memberRepository.save(member1);

        Member member2 = Member.builder()
                .email("duplicate@example.com") // 중복 이메일
                .password("pad456")
                .signType(SignType.KAKAO)
                .guiltyFreeMode(false)
                .memberName("중복2")
                .role(Role.USER)
                .build();

        // When & Then
        // 중복 이메일 저장은 DataIntegrityViolationException을 발생시켜야 함
        assertThrows(DataIntegrityViolationException.class, () -> {
            memberRepository.saveAndFlush(member2); //saveFlush로 바로 에러 발생시킬 수 있게 함. 서비스에서는 MemberHandler로 오류 반환
        });
    }
}