package com.example.backend_monitor.repositories;

import com.example.backend_monitor.entities.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProjectionRepository extends JpaRepository<UserProjection, Integer> {
}
