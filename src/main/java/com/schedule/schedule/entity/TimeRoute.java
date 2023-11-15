package com.schedule.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "route_time")
public class TimeRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "route_id", referencedColumnName="id")
    private Route route;
    @Column(name = "time_total")
    private Integer timeTotal;
    @Column(name = "time_obk")
    private Integer timeObk;
    @Column(name = "time_flights")
    private Integer timeFlights;
}
