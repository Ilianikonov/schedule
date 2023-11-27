package com.schedule.dao;

import com.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query("Select id from Schedule order by date desc limit 2")
    List<Long> getScheduleByDateOrderByDateDesc ();

    Schedule getScheduleById(long id);
}
