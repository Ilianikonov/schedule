package com.schedule.schedule.controller;

import com.schedule.schedule.entity.Schedule;
import com.schedule.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ScheduleControllerImpl implements ScheduleController {
private final ScheduleService scheduleService;

    @Override
    public void uploadSchedule(MultipartFile schedule) throws IOException {
scheduleService.uploadSchedule(schedule.getInputStream());
    }

    @Override
    public Schedule getSchedule(Data data) {
        return null;
    }
}

