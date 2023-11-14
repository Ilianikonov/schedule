package com.schedule.schedule.dao;

import com.schedule.schedule.entity.Filter;
import com.schedule.schedule.entity.Route;
import com.schedule.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Route getRouteByNumber(String number);
}
