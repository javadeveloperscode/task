package com.tasks.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class TelegramService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${telegram.bot.token:}")
    private String botToken;

    public String detectChatId() {
        if (botToken.isBlank()) return null;
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/getUpdates?limit=1&offset=-1";
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resp = restTemplate.getForObject(url, java.util.Map.class);
            if (resp == null || !Boolean.TRUE.equals(resp.get("ok"))) return null;
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> results =
                    (java.util.List<java.util.Map<String, Object>>) resp.get("result");
            if (results == null || results.isEmpty()) return null;
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> msg =
                    (java.util.Map<String, Object>) results.get(0).get("message");
            if (msg == null) return null;
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> chat =
                    (java.util.Map<String, Object>) msg.get("chat");
            if (chat == null) return null;
            return String.valueOf(chat.get("id"));
        } catch (Exception e) {
            log.error("Ошибка getUpdates: {}", e.getMessage());
            return null;
        }
    }

    public void sendMessage(String chatId, String text) {
        if (botToken.isBlank()) {
            log.warn("Telegram bot token не настроен");
            return;
        }
        try {
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            var headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            var body = java.util.Map.of("chat_id", chatId, "text", text, "parse_mode", "HTML");
            var request = new org.springframework.http.HttpEntity<>(body, headers);
            restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            log.error("Ошибка отправки Telegram уведомления в чат {}: {}", chatId, e.getMessage());
        }
    }
}