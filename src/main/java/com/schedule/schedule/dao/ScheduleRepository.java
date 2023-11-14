package com.schedule.schedule.dao;

import com.schedule.schedule.entity.Filter;
import com.schedule.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
 List<Schedule> getSchedulesByDate(LocalDateTime date);

}
