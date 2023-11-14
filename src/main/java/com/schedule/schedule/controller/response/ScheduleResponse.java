package com.schedule.schedule.controller.response;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ScheduleResponse {
    private long id;
    private Date date;
    private DepoResponse depoResponse;
    private RouteResponse routeResponse;
    private Map<String,Integer> time;
}
