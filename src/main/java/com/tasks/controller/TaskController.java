package com.tasks.controller;

import com.tasks.entity.Task;
import com.tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public String list(Model model) {
        List<Task> daily = taskService.findDaily();
        List<Task> global = taskService.findGlobal();
        List<Task> all = taskService.findAll();

        model.addAttribute("dailyTasks", daily.stream().map(TaskListItem::from).toList());
        model.addAttribute("globalTasks", global.stream().map(TaskListItem::from).toList());
        model.addAttribute("hasDailyTasks", !daily.isEmpty());
        model.addAttribute("hasGlobalTasks", !global.isEmpty());

        model.addAttribute("todoCount", all.stream().filter(t -> t.getStatus() == Task.Status.TODO).count());
        model.addAttribute("inProgressCount", all.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count());
        model.addAttribute("doneCount", all.stream().filter(t -> t.getStatus() == Task.Status.DONE).count());
        model.addAttribute("overdueCount", all.stream().filter(Task::isOverdue).count());
        return "tasks/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(defaultValue = "DAILY") Task.Type type, Model model) {
        Task task = new Task();
        task.setType(type);
        model.addAttribute("form", buildForm(task, "Новая задача", true));
        return "tasks/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute Task task, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("form", buildForm(task, "Новая задача", true));
            return "tasks/form";
        }
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Task task = taskService.findById(id);
        model.addAttribute("form", buildForm(task, "Редактировать задачу", false));
        return "tasks/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Task task,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("form", buildForm(task, "Редактировать задачу", false));
            return "tasks/form";
        }
        task.setId(id);
        taskService.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/end-time")
    public String updateEndTime(@PathVariable Long id, @RequestParam String time) {
        Task task = taskService.findById(id);
        if (task.getEndDate() != null) {
            String[] parts = time.split(":");
            task.setEndDate(task.getEndDate()
                    .withHour(Integer.parseInt(parts[0]))
                    .withMinute(Integer.parseInt(parts[1])));
            taskService.save(task);
        }
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Task.Status status) {
        Task task = taskService.findById(id);
        task.setStatus(status);
        taskService.save(task);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        taskService.delete(id);
        return "redirect:/tasks";
    }

    private TaskFormModel buildForm(Task task, String formTitle, boolean isNew) {
        return TaskFormModel.from(task, formTitle, isNew,
                buildStatuses(task.getStatus()),
                buildTypes(task.getType()));
    }

    private List<Map<String, Object>> buildStatuses(Task.Status selected) {
        return Arrays.stream(Task.Status.values())
                .map(s -> Map.<String, Object>of(
                        "value", s.name(),
                        "label", s.getLabel(),
                        "selected", s == selected
                ))
                .toList();
    }

    private List<Map<String, Object>> buildTypes(Task.Type selected) {
        return Arrays.stream(Task.Type.values())
                .map(t -> Map.<String, Object>of(
                        "value", t.name(),
                        "label", t.getLabel(),
                        "selected", t == selected
                ))
                .toList();
    }
}