package com.schedule.schedule.service.entityService;

import com.schedule.schedule.entity.Route;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DepoService {
    private String name;
    private List<RouteService> routeServices = new ArrayList<>();
}
