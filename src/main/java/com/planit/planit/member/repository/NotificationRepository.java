package com.planit.planit.member.repository;

import com.planit.planit.member.Member;
import com.planit.planit.member.association.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByMember(Member member);
}
