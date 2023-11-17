package com.schedule.schedule.dao;

import com.schedule.schedule.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Route getRouteByNumber(String number);
}
