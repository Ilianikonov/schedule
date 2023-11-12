package com.schedule.schedule.service.entityService;

import lombok.Data;

import java.util.List;
@Data
public class DepoDto {
    private String name;
    private List<RouteDto> routeDto;
}
