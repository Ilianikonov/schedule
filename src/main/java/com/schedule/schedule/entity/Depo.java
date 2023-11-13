package com.schedule.schedule.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "depo")
public class Depo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;
}
