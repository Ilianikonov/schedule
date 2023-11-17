package com.schedule.schedule.controller;

import com.schedule.schedule.controller.convert.ConvertController;
import com.schedule.schedule.controller.request.FilterRequest;
import com.schedule.schedule.controller.response.DepoResponse;
import com.schedule.schedule.controller.response.RouteResponse;
import com.schedule.schedule.controller.response.ScheduleResponse;
import com.schedule.schedule.dto.*;
import com.schedule.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ScheduleControllerImpl implements ScheduleController {
private final ScheduleService scheduleService;
private final ConvertController convertController;

    @Override
    public void uploadSchedule(MultipartFile schedule) throws IOException {
scheduleService.uploadSchedule(schedule.getInputStream());
    }

    @Override
    public List<Map<String,Object>> getSchedule(FilterRequest filterRequest) {
        List<ScheduleDto> scheduleDtoList = scheduleService.getSchedule(convertController.convertToFilterDto(filterRequest));
        return convertController.convertToScheduleResponse(scheduleDtoList);
    }

    @Override
    public List<Map<String,Object>> getCurrentSchedule() {
        List<ScheduleDto> scheduleDtoList = scheduleService.getCurrentSchedule();
        return convertController.convertToScheduleResponse(scheduleDtoList);
    }
}

