package com.example.backend_auth.services;

import com.example.backend_auth.config.RabbitMQConfig;
import com.example.backend_auth.dtos.SyncEvent;
import com.example.backend_auth.entitites.Credentials;
import com.example.backend_auth.entitites.UserStatus;
import com.example.backend_auth.repositories.CredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileSyncListener {
    private final CredentialsRepository repo;

    @RabbitListener(queues = RabbitMQConfig.PROFILE_READY_QUEUE)
    public void handleProfileReady(SyncEvent event) {
        Credentials c = repo.findById(event.userId()).orElse(null);
        if (c == null) return;

        c.setStatus(UserStatus.ACTIVE);
        repo.save(c);

        log("Activated user " + c.getUsername());
    }

    private void log(String msg) {
        System.out.println("[AuthSync] " + msg);
    }
}
