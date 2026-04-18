package com.tasks.service;

import com.tasks.entity.Task;
import com.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findDaily() {
        return taskRepository.findAllByTypeOrderByEndDateAsc(Task.Type.DAILY);
    }

    public List<Task> findGlobal() {
        return taskRepository.findAllByTypeOrderByStatusAsc(Task.Type.GLOBAL);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена: " + id));
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
}