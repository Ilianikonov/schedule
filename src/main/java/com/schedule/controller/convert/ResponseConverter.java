package com.schedule.controller.convert;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.schedule.controller.request.FilterRequest;
import com.schedule.controller.response.DepoResponse;
import com.schedule.controller.response.RouteResponse;
import com.schedule.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ResponseConverter {
    private final RuleBasedNumberFormat nf;

    public List<Map<String,Object>> convertToListMap(List<ScheduleDto> scheduleDtoList){
        List<Map<String,Object>> scheduleResponseList = new ArrayList<>();
        for (ScheduleDto scheduleDto:scheduleDtoList) {
            for (DepoDto depoDto: scheduleDto.getDepoDto()) {
                for (RouteDto routeDto : depoDto.getRouteDto()) {

                    Map<String,Object> scheduleResponse = new  LinkedHashMap<>();
                    scheduleResponse.put("id", scheduleDto.getId());
                    scheduleResponse.put("date", scheduleDto.getDate());

                    DepoResponse depoResponse = new DepoResponse();
                    depoResponse.setName(depoDto.getName());
                    depoResponse.setId(depoDto.getId());
                    scheduleResponse.put("depo", depoResponse);

                    RouteResponse routeResponse = new RouteResponse();
                    routeResponse.setId(routeDto.getId());
                    routeResponse.setNumber(routeDto.getNumber());
                    scheduleResponse.put("route", routeResponse);

                    for (TimeDto timeDto : routeDto.getTimeDto()) {
                        String timeName;
                        if(!timeDto.getName().equals("сутки")){
                            timeName = convertToTimeResponse(timeDto.getName());
                        } else {
                            timeName = "totalDay";
                        }
                        scheduleResponse.put(timeName, timeDto.getTotal());
                        scheduleResponse.put(timeName + "Obk", timeDto.getObk());
                        if (timeDto.getFlights() != null) {
                            scheduleResponse.put("planFlights", timeDto.getFlights());
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
        filterDto.setDateEnd(filterRequest.getDate_end() == null ? LocalDate.now() : filterRequest.getDate_end());
        filterDto.setDepo(filterRequest.getDepo());
        filterDto.setRoute(filterRequest.getRoute());
        return filterDto;
    }

    private String convertToTimeResponse(String time){
        String[] s = time.split(":");
        String hour;
        if(nf.format(Integer.valueOf(s[0])).equals("zero")){
            hour = "twentyFour";
        } else if(nf.format(Integer.valueOf(s[0])).contains("-")){
            String[] hourOne = nf.format(Integer.valueOf(s[0])).split("-");
            hour = hourOne[0] + hourOne[1].substring(0, 1).toUpperCase() + hourOne[1].substring(1);
        } else {
            hour = nf.format(Integer.valueOf(s[0]));
        }
        String min = nf.format(Integer.valueOf(s[1])).substring(0, 1).toUpperCase() + nf.format(Integer.valueOf(s[1])).substring(1);
        if(min.equals("Zero")){
            return  hour;
        } else {
            return hour + min;
        }
    }
}
