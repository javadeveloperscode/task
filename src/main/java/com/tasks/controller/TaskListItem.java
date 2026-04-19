package com.tasks.controller;

import com.tasks.entity.Task;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TaskListItem {
    public Long id;
    public String title;
    public String description;
    public String startDate;
    public String endDate;
    public String endDateOnly;
    public String endTime;
    public String statusValue;
    public String statusLabel;
    public boolean overdue;
    public boolean done;
    public boolean global;
    public String priorityValue;
    public boolean priorityHigh;
    public boolean priorityMedium;
    public boolean priorityLow;
    public List<Map<String, Object>> statuses;

    public String startDateIso;
    public String endDateIso;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("d MMM HH:mm", new Locale("ru"));
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM", new Locale("ru"));
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter ISO_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static TaskListItem from(Task task) {
        TaskListItem item = new TaskListItem();
        item.id = task.getId();
        item.title = task.getTitle();
        item.description = task.getDescription();
        item.startDate = task.getStartDate() != null ? task.getStartDate().format(FMT) : "";
        item.startDateIso = task.getStartDate() != null ? task.getStartDate().format(ISO_FMT) : "";
        item.endDate = task.getEndDate() != null ? task.getEndDate().format(FMT) : "";
        item.endDateIso = task.getEndDate() != null ? task.getEndDate().format(ISO_FMT) : "";
        item.endDateOnly = task.getEndDate() != null ? task.getEndDate().format(DATE_FMT) : null;
        item.endTime = task.getEndDate() != null ? task.getEndDate().format(TIME_FMT) : null;
        item.statusValue = task.getStatus().name();
        item.statusLabel = task.getStatus().getLabel();
        item.overdue = task.isOverdue();
        item.done = task.isDone();
        item.global = task.isGlobal();
        item.priorityValue = task.getPriority().name();
        item.priorityHigh = task.getPriority() == Task.Priority.HIGH;
        item.priorityMedium = task.getPriority() == Task.Priority.MEDIUM;
        item.priorityLow = task.getPriority() == Task.Priority.LOW;
        item.statuses = Arrays.stream(Task.Status.values())
                .map(s -> Map.<String, Object>of(
                        "value", s.name(),
                        "label", s.getLabel(),
                        "selected", s == task.getStatus()
                ))
                .toList();
        return item;
    }
}