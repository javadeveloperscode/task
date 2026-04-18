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
    public List<Map<String, Object>> statuses;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("d MMM HH:mm", new Locale("ru"));
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("d MMM", new Locale("ru"));
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    public static TaskListItem from(Task task) {
        TaskListItem item = new TaskListItem();
        item.id = task.getId();
        item.title = task.getTitle();
        item.description = task.getDescription();
        item.startDate = task.getStartDate() != null ? task.getStartDate().format(FMT) : "";
        item.endDate = task.getEndDate() != null ? task.getEndDate().format(FMT) : "";
        item.endDateOnly = task.getEndDate() != null ? task.getEndDate().format(DATE_FMT) : "";
        item.endTime = task.getEndDate() != null ? task.getEndDate().format(TIME_FMT) : "";
        item.statusValue = task.getStatus().name();
        item.statusLabel = task.getStatus().getLabel();
        item.overdue = task.isOverdue();
        item.done = task.isDone();
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