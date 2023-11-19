package com.schedule.controller;

import com.schedule.controller.convert.ResponseConverter;
import com.schedule.controller.request.FilterRequest;
import com.schedule.dto.ScheduleDto;
import com.schedule.exception.FilterFaultException;
import com.schedule.service.ScheduleServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ScheduleControllerImpl implements ScheduleController {
private final ScheduleServiceImpl scheduleServiceImpl;
private final ResponseConverter responseConverter;

    @Override
    public void uploadSchedule(MultipartFile schedule) throws IOException {
        scheduleServiceImpl.uploadSchedule(schedule.getInputStream());
    }

    @Override
    public List<Map<String,Object>> getSchedule(FilterRequest filterRequest) {
        validateFilterRequest(filterRequest);
        List<ScheduleDto> scheduleDtoList = scheduleServiceImpl.getSchedule(responseConverter.convertToFilterDto(filterRequest));
        return responseConverter.convertToScheduleResponse(scheduleDtoList);
    }

    @Override
    public List<Map<String,Object>> getCurrentSchedule() {
        List<ScheduleDto> scheduleDtoList = scheduleServiceImpl.getCurrentSchedule();
        return responseConverter.convertToScheduleResponse(scheduleDtoList);
    }
    private void validateFilterRequest(FilterRequest filterRequest) {
        if (filterRequest.getDate_start() == null && filterRequest.getDate_end() == null && filterRequest.getDepo() == null && filterRequest.getRoute() == null){
            throw new FilterFaultException("Фильтр пуст, укажите хотя бы один параметр!");
        }
    }
}

