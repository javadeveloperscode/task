package com.tasks.repository;

import com.tasks.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByTypeOrderByEndDateAsc(Task.Type type);
    List<Task> findAllByTypeOrderByStatusAsc(Task.Type type);
}