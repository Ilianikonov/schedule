package com.schedule.schedule.service.dtoService;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class RouteDto {
    private String number;
    private List <TimeDto> timeDto = new ArrayList<>();
}
