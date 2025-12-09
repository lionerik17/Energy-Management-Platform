package com.example.backend_device.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DevicesUsers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevicesUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "id_device", nullable = false)
    private Integer idDevice;

    @Column(name = "id_user", nullable = false)
    private Integer idUser;
}
