package com.tasks.controller;

import com.tasks.entity.Task;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TaskFormModel {
    public Long taskId;
    public String title;
    public String description;
    public String startDate;
    public String endDate;
    public String formTitle;
    public boolean isNew;
    public List<Map<String, Object>> statuses;

    private static final DateTimeFormatter DATETIME_LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static TaskFormModel from(Task task, String formTitle, boolean isNew,
                                     List<Map<String, Object>> statuses) {
        TaskFormModel m = new TaskFormModel();
        m.taskId = task.getId();
        m.title = task.getTitle() != null ? task.getTitle() : "";
        m.description = task.getDescription() != null ? task.getDescription() : "";
        m.startDate = task.getStartDate() != null ? task.getStartDate().format(DATETIME_LOCAL) : "";
        m.endDate = task.getEndDate() != null ? task.getEndDate().format(DATETIME_LOCAL) : "";
        m.formTitle = formTitle;
        m.isNew = isNew;
        m.statuses = statuses;
        return m;
    }
}