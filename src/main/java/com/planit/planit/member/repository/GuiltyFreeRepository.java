package com.planit.planit.member.repository;

import com.planit.planit.member.association.GuiltyFree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuiltyFreeRepository extends JpaRepository<GuiltyFree, Long> {
}
