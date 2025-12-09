package com.example.backend_device.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Devices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(nullable = false)
    private String name;

    @Column(name = "max_consumption_value", nullable = false)
    private Integer maxConsumptionValue;
}
