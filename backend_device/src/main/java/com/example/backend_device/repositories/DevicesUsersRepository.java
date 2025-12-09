package com.example.backend_device.repositories;

import com.example.backend_device.entities.Device;
import com.example.backend_device.entities.DevicesUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface DevicesUsersRepository extends JpaRepository<DevicesUsers, Integer> {
    @Query("""
        SELECT d
        FROM Device d
        JOIN DevicesUsers du ON d.id = du.idDevice
        WHERE du.idUser = :userId
    """)
    List<Device> getDevicesForUser(@Param("userId") Integer userId);
    @Modifying
    @Transactional
    void deleteByIdUserAndIdDevice(Integer userId, Integer deviceId);
    @Modifying
    @Transactional
    void deleteByIdUser(Integer userId);
    @Modifying
    @Transactional
    void deleteByIdDevice(Integer deviceId);
}
