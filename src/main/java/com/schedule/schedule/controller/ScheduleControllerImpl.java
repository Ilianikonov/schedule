package com.schedule.schedule.controller;

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

    @Override
    public void uploadSchedule(MultipartFile schedule) throws IOException {
scheduleService.uploadSchedule(schedule.getInputStream());
    }

    @Override
    public List <ScheduleResponse> getSchedule(FilterRequest filterRequest) throws ParseException {
        List<ScheduleDto> scheduleDtoList = scheduleService.getSchedule(convertToFilterDto(filterRequest));
        return convertToScheduleResponse(scheduleDtoList);
    }
    private List<ScheduleResponse> convertToScheduleResponse(List<ScheduleDto> scheduleDtoList){
        List<ScheduleResponse> scheduleResponseList = new ArrayList<>();

        for (ScheduleDto scheduleDto:scheduleDtoList) {
            long id = scheduleDto.getId();
            LocalDate localDate = scheduleDto.getDate();

            for (DepoDto depoDto: scheduleDto.getDepoDto()) {
                DepoResponse depoResponse = new DepoResponse();
                depoResponse.setName(depoDto.getName());
                depoResponse.setId(depoDto.getId());

                for (RouteDto routeDto : depoDto.getRouteDto()) {
                    RouteResponse routeResponse = new RouteResponse();
                    routeResponse.setId(routeDto.getId());
                    routeResponse.setNumber(routeDto.getNumber());
                    Map<String, Integer> time = new LinkedHashMap<>();
                    for (TimeDto timeDto : routeDto.getTimeDto()) {
                        String timeName = timeDto.getName();

                        time.put(timeName + "Total", timeDto.getTotal());
                        time.put(timeName + "Obk", timeDto.getObk());
                        if (timeDto.getFlights() != null) {
                            time.put(timeName + "Flights", timeDto.getFlights());
                        }
                    }
                    ScheduleResponse scheduleResponse = new ScheduleResponse();
                    scheduleResponse.setId(id);
                    scheduleResponse.setDate(localDate);
                    scheduleResponse.setDepoResponse(depoResponse);
                    scheduleResponse.setRouteResponse(routeResponse);
                    scheduleResponse.setTime(time);
                    scheduleResponseList.add(scheduleResponse);
                }
            }
        }
        return scheduleResponseList;
    }
    private FilterDto convertToFilterDto(FilterRequest filterRequest){
        FilterDto filterDto = new FilterDto();
        filterDto.setDateStart(filterRequest.getDate_start());
        filterDto.setDateEnd(filterRequest.getDate_end());
        filterDto.setDepo(filterRequest.getDepo());
        filterDto.setRoute(filterRequest.getRoute());
        return filterDto;
    }

    @Override
    public List <ScheduleResponse> getCurrentSchedule() throws ParseException {
        List<ScheduleDto> scheduleDtoList = scheduleService.getCurrentSchedule();
        return convertToScheduleResponse(scheduleDtoList);
    }
}

