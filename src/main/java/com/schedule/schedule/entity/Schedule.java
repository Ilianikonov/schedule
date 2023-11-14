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
    @JoinColumn(name = "id", nullable = false)
    private long depoId;
    @JoinColumn(name = "id", nullable = false)
    private long routeId;
    @Column(name = "time_total")
    private Integer timeTotal;
    @Column(name = "time_obk")
    private Integer timeObk;
    @Column(name = "time_flights")
    private Integer timeFlights;
}
