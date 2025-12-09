package com.example.backend_user.services;

import com.example.backend_user.config.RabbitMQConfig;
import com.example.backend_user.dtos.SyncEvent;
import com.example.backend_user.entities.Role;
import com.example.backend_user.entities.User;
import com.example.backend_user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepo;
    private final RabbitTemplate rabbit;

    @Transactional
    public void handleUserCreated(SyncEvent event) {
        User u = new User();
        String username = event.data().get("username").toString();
        String role = event.data().get("role").toString();
        Integer age = (Integer) event.data().get("age");

        u.setId(event.userId());
        u.setUsername(username);
        u.setRole(Role.valueOf(role));
        u.setAge(age);

        rabbit.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.USER_PROFILE_READY,
                new SyncEvent("USER_PROFILE_READY", u.getId(), Map.of())
        );

        userRepo.save(u);
        log("Created user from auth: " + u.getUsername());
    }

    @Transactional
    public void handleUserUpdated(SyncEvent event) {
        User u = userRepo.findById(event.userId()).orElse(null);
        if (u == null) return;

        String username = event.data().get("username").toString();
        String role = event.data().get("role").toString();

        u.setUsername(username);
        u.setRole(Role.valueOf(role));

        userRepo.save(u);
        log("Updated user from auth: " + u.getUsername());
    }

    @Transactional
    public void handleUserDeleted(SyncEvent event) {
        userRepo.deleteById(event.userId());
        log("Deleted user from auth: id=" + event.userId());
    }

    private void log(String msg) {
        System.out.println("[UserSync] " + msg);
    }
}
