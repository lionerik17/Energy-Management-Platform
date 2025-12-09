package com.example.backend_auth.services;

import com.example.backend_auth.dtos.*;
import com.example.backend_auth.entitites.Credentials;
import com.example.backend_auth.entitites.Role;
import com.example.backend_auth.entitites.UserStatus;
import com.example.backend_auth.jwt.JwtService;
import com.example.backend_auth.repositories.CredentialsRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CredentialsRepository credentialsRepo;
    private final SyncPublisher syncPublisher;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterDTO dto) {
        if (credentialsRepo.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        Credentials entity = credentialsRepo.save(
                Credentials.builder()
                        .username(dto.username())
                        .password(encoder.encode(dto.password()))
                        .role(Role.CLIENT)
                        .status(UserStatus.PENDING)
                        .build()
        );

        syncPublisher.userCreated(entity, dto.age());

        String token = jwtService.generate(entity.getId(), entity.getUsername(), entity.getRole().name());
        return new AuthResponse(entity.getId(), token, entity.getUsername(), entity.getRole(), JwtService.expirationMs);
    }

    @Transactional
    public CredentialsResponse update(Integer id, AuthDTO dto) {
        Credentials entity = credentialsRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Credentials not found"));

        if (entity == null) {
            throw new IllegalArgumentException("Invalid username");
        }

        entity.setUsername(dto.username());
        entity.setPassword(encoder.encode(dto.password()));
        entity.setRole(dto.role());

        syncPublisher.userUpdated(entity);
        credentialsRepo.save(entity);

        return new CredentialsResponse(entity.getId(), entity.getUsername(), entity.getPassword(), entity.getRole());
    }

    @Transactional
    public void delete(Integer id) {
        Credentials entity = credentialsRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Credentials not found"));

        if (entity == null) {
            throw new IllegalArgumentException("Invalid username");
        }

        syncPublisher.userDeleted(entity);
        credentialsRepo.delete(entity);
    }

    public AuthResponse login(LoginDTO req) {
        Credentials entity = credentialsRepo.findByUsername(req.username());

        if (!encoder.matches(req.password(), entity.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (entity.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("User profile not ready yet. Try again shortly.");
        }

        String token = jwtService.generate(entity.getId(), entity.getUsername(), entity.getRole().name());
        return new AuthResponse(entity.getId(), token, entity.getUsername(), entity.getRole(), JwtService.expirationMs);
    }

    public Claims verifyAndExtract(String header) {
        String token = extractToken(header);
        if (token == null) return null;

        return jwtService.verify(token);
    }

    public boolean authorizeRequest(String header, String requiredRole) {
        String token = extractToken(header);
        if (token == null) return false;

        Claims claims = verifyToken(token);
        if (claims == null) return false;

        String tokenRole = claims.get("role", String.class);
        return requiredRole.equals(tokenRole);
    }

    private Claims verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return jwtService.verify(token);
    }

    private String extractToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
