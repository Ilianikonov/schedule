package com.schedule.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "depo")
public class Depo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinColumn(name = "schedule_id", referencedColumnName="id", nullable = false)
    private Schedule schedule;
    @OneToMany(mappedBy = "depo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Route> route;

}
