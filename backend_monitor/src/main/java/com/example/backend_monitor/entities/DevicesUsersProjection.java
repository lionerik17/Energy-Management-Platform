package com.example.backend_monitor.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "DevicesUsersProjection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicesUsersProjection {
    @Id
    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "user_id")
    private Integer userId;
}
