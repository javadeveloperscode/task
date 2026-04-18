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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final TaskService taskService;
    private final UserService userService;

    private static final ZoneId MSK = ZoneId.of("Europe/Moscow");
    private static final int CHART_DAYS = 14;

    @GetMapping
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        AppUser user = userService.getByUsername(userDetails.getUsername());
        List<Task> all = taskService.findAll();

        LocalDateTime nowMsk = LocalDateTime.now(MSK);
        LocalDate todayMsk = nowMsk.toLocalDate();

        long done       = all.stream().filter(t -> t.getStatus() == Task.Status.DONE).count();
        long inProgress = all.stream().filter(t -> t.getStatus() == Task.Status.IN_PROGRESS).count();
        long missed     = all.stream()
                .filter(t -> t.getStatus() == Task.Status.TODO
                        && t.getEndDate() != null
                        && t.getEndDate().isBefore(nowMsk))
                .count();
        long total = all.size();

        // Chart: last CHART_DAYS days
        DateTimeFormatter labelFmt = DateTimeFormatter.ofPattern("d MMM", new Locale("ru"));
        List<String> chartLabels = new ArrayList<>();
        List<Long> chartData = new ArrayList<>();

        for (int i = CHART_DAYS - 1; i >= 0; i--) {
            LocalDate day = todayMsk.minusDays(i);
            chartLabels.add(day.format(labelFmt));
            long count = all.stream()
                    .filter(t -> t.getCompletedAt() != null
                            && t.getCompletedAt().toLocalDate().equals(day))
                    .count();
            chartData.add(count);
        }

        String labelsJson = chartLabels.stream()
                .map(l -> "\"" + l + "\"")
                .collect(Collectors.joining(",", "[", "]"));
        String dataJson = chartData.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));

        model.addAttribute("username", user.getUsername());
        model.addAttribute("usernameInitial", user.getUsername().substring(0, 1).toUpperCase());
        model.addAttribute("streak", user.getStreak());
        model.addAttribute("done", done);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("missed", missed);
        model.addAttribute("total", total);
        model.addAttribute("chartLabels", labelsJson);
        model.addAttribute("chartData", dataJson);

        return "profile";
    }
}