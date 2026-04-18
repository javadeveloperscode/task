package com.tasks.controller;

import com.tasks.entity.AppUser;
import com.tasks.entity.Task;
import com.tasks.service.TaskService;
import com.tasks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final TaskService taskService;
    private final UserService userService;

    private static final ZoneId MSK = ZoneId.of("Europe/Moscow");

    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        AppUser user = userService.getByUsername(userDetails.getUsername());
        List<Task> all = taskService.findAll();

        LocalDateTime nowMsk = LocalDateTime.now(MSK);

        long done      = all.stream().filter(t -> t.getStatus() == Task.Status.DONE).count();
        long inProgress = all.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count();
        long missed    = all.stream()
                .filter(t -> t.getStatus() == Task.Status.TODO
                        && t.getEndDate() != null
                        && t.getEndDate().isBefore(nowMsk))
                .count();
        long total     = all.size();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("usernameInitial", user.getUsername().substring(0, 1).toUpperCase());
        model.addAttribute("streak", user.getStreak());
        model.addAttribute("done", done);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("missed", missed);
        model.addAttribute("total", total);

        return "profile";
    }
}