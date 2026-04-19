package com.tasks.service;

import com.tasks.entity.AppUser;
import com.tasks.entity.Task;
import com.tasks.repository.TaskRepository;
import com.tasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final ZoneId MSK = ZoneId.of("Europe/Moscow");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d MMM", new Locale("ru"));

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TelegramService telegramService;

    @Scheduled(fixedDelay = 60_000)
    public void sendStartNotifications() {
        List<AppUser> subscribers = userRepository.findAllByTelegramChatIdNotNull();
        if (subscribers.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now(MSK);
        LocalDateTime from = now.plusMinutes(29);
        LocalDateTime to   = now.plusMinutes(31);

        List<Task> upcoming = taskRepository.findAllByStartNotifiedFalseAndStartDateBetween(from, to);
        if (upcoming.isEmpty()) return;

        for (Task task : upcoming) {
            String time = task.getStartDate().format(TIME_FMT);
            String date = task.getStartDate().format(DATE_FMT);
            String text = "⏰ <b>Через 30 минут</b> начинается задача:\n\n"
                    + "<b>" + escapeHtml(task.getTitle()) + "</b>\n"
                    + "🕐 " + date + " в " + time;
            if (task.getDescription() != null && !task.getDescription().isBlank()) {
                text += "\n📝 " + escapeHtml(task.getDescription());
            }

            for (AppUser user : subscribers) {
                telegramService.sendMessage(user.getTelegramChatId(), text);
            }

            task.setStartNotified(true);
            taskRepository.save(task);
            log.info("Уведомление отправлено для задачи #{}: {}", task.getId(), task.getTitle());
        }
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}