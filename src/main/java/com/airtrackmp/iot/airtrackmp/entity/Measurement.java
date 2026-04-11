package com.airtrackmp.iot.airtrackmp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pm25")
    private Float pm25;

    @Column(name = "pm10")
    private Float pm10;

    private Float temperature;
    private Float humidity;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    // Relación con Node
    @ManyToOne
    @JoinColumn(name = "node_id", nullable = false)
    private Node node;
}
