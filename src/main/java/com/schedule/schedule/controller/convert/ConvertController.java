package com.schedule.schedule.controller.convert;

import com.schedule.schedule.controller.request.FilterRequest;
import com.schedule.schedule.controller.response.DepoResponse;
import com.schedule.schedule.controller.response.RouteResponse;
import com.schedule.schedule.dto.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Component
public class ConvertController {
    public List<Map<String,Object>> convertToScheduleResponse(List<ScheduleDto> scheduleDtoList){
        List<Map<String,Object>> scheduleResponseList = new ArrayList<>();

        for (ScheduleDto scheduleDto:scheduleDtoList) {
            Map<String,Object> scheduleResponse = new  LinkedHashMap<>();
            scheduleResponse.put("id", scheduleDto.getId());
            scheduleResponse.put("date", scheduleDto.getDate());

            for (DepoDto depoDto: scheduleDto.getDepoDto()) {
                DepoResponse depoResponse = new DepoResponse();
                depoResponse.setName(depoDto.getName());
                depoResponse.setId(depoDto.getId());
                scheduleResponse.put("depo", depoResponse);

                for (RouteDto routeDto : depoDto.getRouteDto()) {
                    RouteResponse routeResponse = new RouteResponse();
                    routeResponse.setId(routeDto.getId());
                    routeResponse.setNumber(routeDto.getNumber());
                    scheduleResponse.put("route", routeResponse);
                    for (TimeDto timeDto : routeDto.getTimeDto()) {
                        String timeName = timeDto.getName();
                        scheduleResponse.put(timeName + "Total", timeDto.getTotal());
                        scheduleResponse.put(timeName + "Obk", timeDto.getObk());
                        if (timeDto.getFlights() != null) {
                            scheduleResponse.put(timeName + "Flights", timeDto.getFlights());
                        }
                    }
                    scheduleResponseList.add(scheduleResponse);
                }
            }
        }
        return scheduleResponseList;
    }
    public FilterDto convertToFilterDto(FilterRequest filterRequest){
        FilterDto filterDto = new FilterDto();
        filterDto.setDateStart(filterRequest.getDate_start());
        filterDto.setDateEnd(filterRequest.getDate_end());
        filterDto.setDepo(filterRequest.getDepo());
        filterDto.setRoute(filterRequest.getRoute().toString());
        return filterDto;
    }

}
