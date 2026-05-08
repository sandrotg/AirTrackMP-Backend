package com.airtrackmp.iot.airtrackmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type;

    private String message;

    private boolean deleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "node_id", nullable = false)
    private Node node;

    @OneToOne
    @JoinColumn(name = "measurement_id", nullable = false)
    private Measurement measurement;
}
