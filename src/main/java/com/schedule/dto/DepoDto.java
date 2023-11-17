package com.schedule.dto;

import lombok.Data;

import java.util.List;
@Data
public class DepoDto {
    private long id;
    private String name;
    private List<RouteDto> routeDto;
}
