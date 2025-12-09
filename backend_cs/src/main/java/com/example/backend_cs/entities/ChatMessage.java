package com.example.backend_cs.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ChatMessage")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(nullable = false)
    public String sender;

    @Column(nullable = false)
    public String receiver;

    @Column(nullable = false)
    public String message;

    @Column
    public LocalDateTime timestamp;
}
