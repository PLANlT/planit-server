package com.planit.planit.dream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DreamRepository extends JpaRepository<Dream, Long> {
}
