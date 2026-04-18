package com.tasks.service;

import com.tasks.entity.AppUser;
import com.tasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    public AppUser getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    /**
     * @return новое значение стрика если он вырос, иначе -1
     */
    public int recordActivity(String username) {
        AppUser user = getByUsername(username);
        LocalDate today = LocalDate.now();
        LocalDate last = user.getLastActivityDate();

        if (today.equals(last)) {
            return -1; // уже засчитано сегодня
        } else if (last != null && last.equals(today.minusDays(1))) {
            user.setStreak(user.getStreak() + 1);
        } else {
            user.setStreak(1);
        }
        user.setLastActivityDate(today);
        userRepository.save(user);
        return user.getStreak();
    }

    public void register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Пользователь уже существует");
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}