package com.schedule.schedule.dao;

import com.schedule.schedule.entity.Depo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepoRepository extends JpaRepository<Depo, Long> {
    Depo getDepoByName(String name);
}
