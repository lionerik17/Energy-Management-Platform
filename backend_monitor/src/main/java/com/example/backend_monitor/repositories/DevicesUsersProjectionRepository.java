package com.example.backend_monitor.repositories;

import com.example.backend_monitor.entities.DevicesUsersProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DevicesUsersProjectionRepository extends JpaRepository<DevicesUsersProjection, Integer> {
    @Query("""
        SELECT du
        FROM DevicesUsersProjection du
        WHERE du.deviceId = :deviceId
    """)
    DevicesUsersProjection findByDeviceId(@Param("deviceId") Integer deviceId);
    @Modifying
    @Transactional
    void deleteByUserIdAndDeviceId(Integer userId, Integer deviceId);
    @Modifying
    @Transactional
    void deleteByUserId(Integer userId);
    @Modifying
    @Transactional
    void deleteByDeviceId(Integer deviceId);
}
