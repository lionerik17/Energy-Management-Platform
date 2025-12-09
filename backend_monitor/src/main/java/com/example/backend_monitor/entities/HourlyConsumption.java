package com.example.backend_monitor.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HourlyConsumption")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HourlyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private Integer deviceId;

    private LocalDateTime hour;

    @Column(name = "total_consumption")
    private Double totalConsumption;
}

