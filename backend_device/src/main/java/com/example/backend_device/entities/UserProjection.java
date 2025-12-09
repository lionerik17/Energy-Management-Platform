package com.example.backend_device.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UserProjection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProjection {
    @Id
    private Integer id;

    private String username;
}

