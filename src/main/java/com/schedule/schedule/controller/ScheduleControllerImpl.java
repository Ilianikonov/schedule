package com.schedule.schedule.controller;

import com.schedule.schedule.controller.request.FilterRequest;
import com.schedule.schedule.controller.response.ScheduleResponse;
import com.schedule.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleControllerImpl implements ScheduleController {
private final ScheduleService scheduleService;

    @Override
    public void uploadSchedule(MultipartFile schedule) throws IOException {
scheduleService.uploadSchedule(schedule.getInputStream());
    }

    @Override
    public List <ScheduleResponse> getSchedule(FilterRequest filterRequest) {
        return Collections.emptyList();
    }

    @Override
    public List <ScheduleResponse> getCurrentSchedule() {
        return new ArrayList<>();
    }
}

