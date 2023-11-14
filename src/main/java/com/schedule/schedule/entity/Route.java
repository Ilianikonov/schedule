package com.schedule.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "route")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String number;
}
