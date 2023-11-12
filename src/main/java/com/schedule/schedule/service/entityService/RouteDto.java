package com.schedule.schedule.service.entityService;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class RouteDto {
    private String number;
    private List <TimeDto> timeDto = new ArrayList<>();
}
