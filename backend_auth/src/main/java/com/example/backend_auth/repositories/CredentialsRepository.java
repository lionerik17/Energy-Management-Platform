package com.example.backend_auth.repositories;

import com.example.backend_auth.entitites.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialsRepository extends JpaRepository<Credentials, Integer> {
    Credentials findByUsername(String username);
    boolean existsByUsername(String username);
}
