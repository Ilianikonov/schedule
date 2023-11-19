package com.schedule.service;

import com.schedule.dto.FilterDto;
import com.schedule.dto.ScheduleDto;
import com.schedule.exception.DublicateException;
import com.schedule.exception.FilterFaultException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

public interface ScheduleService {
    void uploadSchedule(InputStream schedule) throws IOException, DublicateException;
    List<ScheduleDto> getSchedule(FilterDto filterDto) throws ParseException, FilterFaultException;
    List<ScheduleDto> getCurrentSchedule() throws ParseException;
}
