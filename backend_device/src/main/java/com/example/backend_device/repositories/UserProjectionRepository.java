package com.example.backend_device.repositories;

import com.example.backend_device.entities.UserProjection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProjectionRepository extends JpaRepository<UserProjection, Integer> {
}
