package com.schedule.controller;

import com.schedule.controller.request.FilterRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ScheduleController {

    @PostMapping("/uploadSchedule")
    void uploadSchedule(@RequestParam("schedule") MultipartFile schedule) throws IOException;

    @GetMapping("/getSchedule")
    List<Map<String,Object>> getSchedule(@RequestBody FilterRequest filterRequest);

    @GetMapping("/getScheduleActual")
    List<Map<String,Object>> getCurrentSchedule();
}
