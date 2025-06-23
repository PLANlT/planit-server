package com.planit.planit.task.repository;

import com.planit.planit.task.association.CompletedTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompletedTaskRepository extends JpaRepository<CompletedTask, Long> {
    List<CompletedTask> findAllByTaskIdAndCompletedAt(Long taskId, LocalDate completedAt);
}
