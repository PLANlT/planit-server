package com.planit.planit.member.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.enums.SignType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<Member> findByEmailAndSignType(String email, SignType signType);
    Optional<Member> findByEmailAndInactiveIsNull(String email);
}
