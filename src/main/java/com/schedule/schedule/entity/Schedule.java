package com.schedule.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private LocalDateTime date;
    private String time;
    @OneToOne(targetEntity = Depo.class)
    @JoinColumn(name = "id")
    private long depoId;
    @OneToOne(targetEntity = Route.class)
    @JoinColumn(name = "id", nullable = false)
    private long routeId;
    @Column(name = "time_total")
    private Integer timeTotal;
    @Column(name = "time_obk")
    private Integer timeObk;
}
