package com.tasks.controller;

import com.tasks.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class AuthController {

  private final UserService userService;

  @GetMapping("/login")
  public String loginPage(@RequestParam(required = false) String error,
                          @RequestParam(required = false) String logout,
                          Model model) {
    if (error != null) model.addAttribute("error", "Неверный логин или пароль");
    if (logout != null) model.addAttribute("message", "Вы вышли из системы");
    return "auth/login";
  }

  @GetMapping("/register")
  public String registerPage() {
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String passwordConfirm,
                         Model model) {
    if (!password.equals(passwordConfirm)) {
      model.addAttribute("error", "Пароли не совпадают");
      return "auth/register";
    }
    if (username.isBlank() || password.length() < 4) {
      model.addAttribute("error", "Логин не может быть пустым, пароль — минимум 4 символа");
      return "auth/register";
    }
    try {
      userService.register(username, password);
      return "redirect:/login?registered";
    } catch (IllegalArgumentException e) {
      model.addAttribute("error", e.getMessage());
      return "auth/register";
    }
  }
}