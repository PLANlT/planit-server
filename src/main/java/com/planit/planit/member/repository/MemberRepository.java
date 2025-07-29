package com.planit.planit.member.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.SignType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 활성 회원만 조회 (inactive가 null인 회원)
    Optional<Member> findByEmailAndInactiveIsNull(String email);
    Boolean existsByEmail(String email);
    Optional<Member> findByEmailAndSignType(String email, SignType signType);
}
