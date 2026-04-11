package com.airtrackmp.iot.airtrackmp.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
