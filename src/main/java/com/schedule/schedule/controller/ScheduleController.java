package com.schedule.schedule.controller;

import com.schedule.schedule.entity.Schedule;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;

public interface ScheduleController {

    @PostMapping("/uploadSchedule")
    void uploadSchedule(@RequestParam("schedule") MultipartFile schedule) throws IOException;

    @GetMapping("/getSchedules/{data}")
    Schedule getSchedule(@PathVariable Data data);

}
