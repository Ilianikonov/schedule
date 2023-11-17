package com.schedule.schedule.controller;

import com.schedule.schedule.controller.request.FilterRequest;
import com.schedule.schedule.controller.response.ScheduleResponse;
import com.schedule.schedule.dto.ScheduleDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ScheduleController {

    @PostMapping("/uploadSchedule")
    void uploadSchedule(@RequestParam("schedule") MultipartFile schedule) throws IOException;

    @GetMapping("/getSchedules")
    List<Map<String,Object>> getSchedule(@RequestBody FilterRequest filterRequest) throws ParseException;
    @GetMapping("/getSchedule")
    List<Map<String,Object>> getCurrentSchedule() throws ParseException;
}
