package com.schedule.controller;

import com.schedule.controller.convert.ConvertController;
import com.schedule.controller.request.FilterRequest;
import com.schedule.dto.ScheduleDto;
import com.schedule.exception.DublicateException;
import com.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ScheduleControllerImpl implements ScheduleController {
private final ScheduleService scheduleService;
private final ConvertController convertController;

    @Override
    public void uploadSchedule(MultipartFile schedule) throws IOException, DublicateException {
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

