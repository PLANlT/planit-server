package com.planit.planit.plan.service;

import com.planit.planit.common.api.plan.PlanHandler;
import com.planit.planit.member.Member;
import com.planit.planit.member.MemberRepository;
import com.planit.planit.plan.Plan;
import com.planit.planit.plan.enums.PlanStatus;
import com.planit.planit.plan.repository.PlanRepository;
import com.planit.planit.web.dto.plan.PlanRequestDTO;
import com.planit.planit.web.dto.plan.PlanResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PlanCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanCommandServiceImpl planCommandService;

    private Member member1;
    private Member member2;
    private Plan plan;


/*------------------------------ SETUP ------------------------------*/

    @BeforeEach
    public void setUp() {
        initMember();                       // 회원 더미데이터 생성
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
    }

    private void initMember() {
        member1 = Member.builder()
                .id(1L)
                .email("xxx@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .build();
        member2 = Member.builder()
                .id(2L)
                .email("yyy@email.com")
                .password("password")
                .guiltyFreeMode(false)
                .build();
    }


/*------------------------------ 플랜 생성 ------------------------------*/

    @Test
    @DisplayName("플랜 생성 (성공)")
    public void createPlanTest_Success() {

        // given
        PlanRequestDTO.PlanDTO planDTO = PlanRequestDTO.PlanDTO.builder()
                .title("제목")
                .motivation("목표")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .startedAt(LocalDate.now())
                .finishedAt(LocalDate.now().plusMonths(1))
                .build();

        plan = Plan.builder()
                .id(1L)
                .title(planDTO.getTitle())
                .motivation(planDTO.getMotivation())
                .icon(planDTO.getIcon())
                .planStatus(planDTO.getPlanStatus())
                .startedAt(planDTO.getStartedAt())
                .finishedAt(planDTO.getFinishedAt())
                .member(member1)
                .build();

        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        // when
        PlanResponseDTO.PlanMetaDTO result = planCommandService.createPlan(1L, planDTO);

        // then
        assertNotNull(result);
        verify(planRepository, times(1)).save(any(Plan.class));
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.IN_PROGRESS);
    }


/*------------------------------ 플랜 수정 ------------------------------*/

    @Test
    @DisplayName("플랜 수정 (성공)")
    public void updatePlanTest_Success() {

        // given
        PlanRequestDTO.PlanDTO planDTO = PlanRequestDTO.PlanDTO.builder()
                .title("수정된 제목")
                .motivation("목표")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .startedAt(LocalDate.now())
                .finishedAt(LocalDate.now().plusMonths(1))
                .build();

        plan = Plan.builder()
                .id(1L)
                .title(planDTO.getTitle())
                .motivation(planDTO.getMotivation())
                .icon(planDTO.getIcon())
                .planStatus(planDTO.getPlanStatus())
                .startedAt(planDTO.getStartedAt())
                .finishedAt(planDTO.getFinishedAt())
                .member(member1)
                .build();

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(planRepository.save(any(Plan.class))).thenReturn(plan);

        // when
        PlanResponseDTO.PlanMetaDTO result = planCommandService.updatePlan(1L, 1L, planDTO);

        // then
        assertNotNull(result);
        verify(planRepository, times(1)).save(any(Plan.class));
        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("플랜 수정 (없는 플랜)")
    public void updatePlanTest_NoPlan_Fail() {

        // given
        PlanRequestDTO.PlanDTO planDTO = PlanRequestDTO.PlanDTO.builder()
                .title("수정된 제목")
                .motivation("목표")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .startedAt(LocalDate.now())
                .finishedAt(LocalDate.now().plusMonths(1))
                .build();

        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.updatePlan(1L, 1L, planDTO));
    }

    @Test
    @DisplayName("플랜 수정 (사용자의 플랜이 아님)")
    public void updatePlanTest_NotMyPlan_Fail() {

        // given
        PlanRequestDTO.PlanDTO planDTO = PlanRequestDTO.PlanDTO.builder()
                .title("수정된 제목")
                .motivation("목표")
                .icon("아이콘")
                .planStatus(PlanStatus.IN_PROGRESS)
                .startedAt(LocalDate.now())
                .finishedAt(LocalDate.now().plusMonths(1))
                .build();

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.updatePlan(2L, 1L, planDTO));
    }



    /*------------------------------ 플랜 완료 ------------------------------*/

    @Test
    @DisplayName("플랜 완료 (성공)")
    public void completePlanTest_Success() {

        // given
        plan = Plan.builder()
                .id(1L)
                .title("제목")
                .motivation("목표")
                .icon("아이콘")
                .planStatus(PlanStatus.ARCHIVED)
                .startedAt(LocalDate.now())
                .finishedAt(LocalDate.now().plusMonths(1))
                .member(member1)
                .build();

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        // when
        PlanResponseDTO.PlanMetaDTO result = planCommandService.completePlan(1L, 1L);

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.ARCHIVED);
    }

    @Test
    @DisplayName("플랜 완료 (없는 플랜)")
    public void completePlanTest_NoPlan_Fail() {
        // given
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.completePlan(1L, 1L));
    }

    @Test
    @DisplayName("플랜 완료 (사용자의 플랜이 아님)")
    public void completePlanTest_NotMyPlan_Fail() {
        // given
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.completePlan(2L, 1L));
    }


/*------------------------------ 플랜 중단 ------------------------------*/

    @Test
    @DisplayName("플랜 중단 (성공)")
    public void pausePlanTest_Success() {

        // given
        plan = Plan.builder()
                .id(1L)
                .title("제목")
                .motivation("목표")
                .icon("아이콘")
                .planStatus(PlanStatus.PAUSED)
                .startedAt(LocalDate.now())
                .finishedAt(LocalDate.now().plusMonths(1))
                .member(member1)
                .build();

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        // when
        PlanResponseDTO.PlanMetaDTO result = planCommandService.pausePlan(1L, 1L);

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.PAUSED);
    }

    @Test
    @DisplayName("플랜 중단 (없는 플랜)")
    public void pausePlanTest_NoPlan_Fail() {
        // given
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.pausePlan(1L, 1L));
    }

    @Test
    @DisplayName("플랜 중단 (사용자의 플랜이 아님)")
    public void pausePlanTest_NotMyPlan_Fail() {
        // given
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.pausePlan(1L, 1L));
    }


/*------------------------------ 플랜 삭제 ------------------------------*/

    @Test
    @DisplayName("플랜 삭제 (성공)")
    public void deletePlanTest_Success() {

        // given
        plan = Plan.builder()
                .id(1L)
                .title("제목")
                .motivation("목표")
                .icon("아이콘")
                .planStatus(PlanStatus.DELETED)
                .startedAt(LocalDate.now())
                .finishedAt(LocalDate.now().plusMonths(1))
                .member(member1)
                .build();

        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));

        // when
        PlanResponseDTO.PlanMetaDTO result = planCommandService.deletePlan(1L, 1L);

        // then
        assertNotNull(result);
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getPlanStatus()).isEqualTo(PlanStatus.DELETED);
    }

    @Test
    @DisplayName("플랜 삭제 (없는 플랜)")
    public void deletePlanTest_NoPlan_Fail() {
        // given
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.deletePlan(1L, 1L));
    }

    @Test
    @DisplayName("플랜 삭제 (사용자의 플랜이 아님)")
    public void deletePlanTest_NotMyPlan_Fail() {
        // given
        when(memberRepository.findById(2L)).thenReturn(Optional.of(member2));
        when(planRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PlanHandler.class, () -> planCommandService.deletePlan(1L, 1L));
    }
}