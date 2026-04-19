package com.tasks.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название обязательно")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "start_notified", nullable = false)
    private boolean startNotified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.DAILY;

    public enum Status {
        TODO("К выполнению"),
        IN_PROGRESS("В работе"),
        DONE("Готово");

        private final String label;
        Status(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Priority {
        HIGH("Высокий"),
        MEDIUM("Средний"),
        LOW("Низкий");

        private final String label;
        Priority(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Type {
        GLOBAL("Глобальная"),
        DAILY("Дневная");

        private final String label;
        Type(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public boolean isOverdue() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        return status != Status.DONE && endDate != null && !now.isBefore(endDate);
    }

    public boolean isDone() {
        return status == Status.DONE;
    }

    public boolean isGlobal() {
        return type == Type.GLOBAL;
    }
}