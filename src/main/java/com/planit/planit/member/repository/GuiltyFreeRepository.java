package com.planit.planit.member.repository;

import com.planit.planit.member.association.GuiltyFree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface GuiltyFreeRepository extends JpaRepository<GuiltyFree, Long> {

    Optional<GuiltyFree> findByMemberIdAndActive(Long memberId, LocalDate active);
}
