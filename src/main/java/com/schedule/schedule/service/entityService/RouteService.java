package com.schedule.schedule.service.entityService;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class RouteService {
    private String number;
    private List <TimeService> timeServices = new ArrayList<>();
}
