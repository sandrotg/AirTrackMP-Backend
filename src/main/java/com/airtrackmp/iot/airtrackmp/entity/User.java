package com.airtrackmp.iot.airtrackmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String email;

    private String password;

    private String role;

    private boolean deleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
