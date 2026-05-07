package com.airtrackmp.iot.airtrackmp.entity;

import com.airtrackmp.iot.airtrackmp.dto.enums.NodeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "nodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer  id;

    private String name;

    private String location;

    private Float latitude;

    private Float longitude;

    private boolean deleted;

    @Enumerated(EnumType.STRING)
    private NodeStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
