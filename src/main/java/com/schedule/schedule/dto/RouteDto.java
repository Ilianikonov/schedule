package com.schedule.schedule.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class RouteDto {
    private long id;
    private String number;
    private List <TimeDto> timeDto = new ArrayList<>();
}
