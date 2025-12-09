package com.example.backend_monitor.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DeviceProjection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceProjection {
    @Id
    private Integer deviceId;
}
