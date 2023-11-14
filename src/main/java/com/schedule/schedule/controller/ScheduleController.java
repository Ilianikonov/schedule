package com.schedule.schedule.controller;

import com.schedule.schedule.controller.request.FilterRequest;
import com.schedule.schedule.controller.response.ScheduleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ScheduleController {

    @PostMapping("/uploadSchedule")
    void uploadSchedule(@RequestParam("schedule") MultipartFile schedule) throws IOException;

    @GetMapping("/getSchedules")
    List<ScheduleResponse> getSchedule(@RequestBody FilterRequest filterRequest);
    @GetMapping("/getSchedule")
    List <ScheduleResponse> getCurrentSchedule();
}
