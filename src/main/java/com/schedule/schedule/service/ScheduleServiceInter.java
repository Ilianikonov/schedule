package com.schedule.schedule.service;

import com.schedule.schedule.dto.FilterDto;
import com.schedule.schedule.dto.ScheduleDto;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

public interface ScheduleServiceInter {
    void uploadSchedule(InputStream schedule) throws IOException;
    List<ScheduleDto> getSchedule(FilterDto filterDto) throws ParseException;
    List<ScheduleDto> getCurrentSchedule() throws ParseException;
}
