package com.schedule.service;

import com.schedule.dto.DepoDto;
import com.schedule.dto.RouteDto;
import com.schedule.dto.ScheduleDto;
import com.schedule.dto.TimeDto;
import com.schedule.entity.Depo;
import com.schedule.entity.Route;
import com.schedule.entity.Schedule;
import com.schedule.entity.TimeRoute;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class Convert {
    public List<TimeRoute> convertToTimeRoute(List<TimeDto> timeDtoList, Route route){
        List<TimeRoute> timeRouteList = new ArrayList<>();
        for (TimeDto timeDto : timeDtoList) {
            TimeRoute timeRoute = new TimeRoute();
            timeRoute.setName(timeDto.getName());
            timeRoute.setRoute(route);
            timeRoute.setTimeTotal(timeDto.getTotal());
            timeRoute.setTimeObk(timeDto.getObk());
            timeRoute.setTimeFlights(timeDto.getFlights());
            timeRouteList.add(timeRoute);
        }
        return timeRouteList;
    }
    public Schedule convertScheduleDtoToSchedule(ScheduleDto scheduleDto){
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDto.getDate());
        schedule.setDepoList(convertToDepo(scheduleDto.getDepoDto(), schedule));
        return schedule;
    }
    public List<Depo> convertToDepo(List<DepoDto> depoDtoList, Schedule schedule){
        List<Depo> depoList = new ArrayList<>();
        for (DepoDto depoDto : depoDtoList) {
            Depo depo = new Depo();
            depo.setName(depoDto.getName());
            depo.setSchedule(schedule);
            depo.setRoute(convertToRoute(depoDto.getRouteDto(), depo));
            depoList.add(depo);
        }
        return depoList;
    }
    public List<Route> convertToRoute(List<RouteDto> routeDtoList, Depo depo){
        List<Route> routeList = new ArrayList<>();
        for (RouteDto routeDto : routeDtoList) {
            Route route = new Route();
            route.setNumber(routeDto.getNumber());
            route.setDepo(depo);
            route.setTimeRoutes(convertToTimeRoute(routeDto.getTimeDto(), route));
            routeList.add(route);
        }
        return routeList;
    }
    public List<ScheduleDto> convertScheduleToScheduleDto(List<Schedule> scheduleList) {
        List<ScheduleDto> scheduleDtoList = new ArrayList<>();
        for (Schedule schedule:scheduleList) {
            ScheduleDto scheduleDto = new ScheduleDto();
            scheduleDto.setId(schedule.getId());
            scheduleDto.setDate(schedule.getDate());
            scheduleDto.setDepoDto(convertToDepoDpo(schedule.getDepoList()));
            scheduleDtoList.add(scheduleDto);
        }
        return scheduleDtoList;
    }
    public List<DepoDto> convertToDepoDpo(List<Depo> depoList){
        List<DepoDto> depoDtoList = new ArrayList<>();
        for (Depo depo: depoList) {
            DepoDto depoDto = new DepoDto();
            depoDto.setId(depo.getId());
            depoDto.setName(depo.getName());
            depoDto.setRouteDto(convertToRouteDto(depo.getRoute()));
            depoDtoList.add(depoDto);
        }
        return depoDtoList;
    }

    public List<RouteDto> convertToRouteDto(List<Route> routeList){
        List<RouteDto> routeDtoList = new ArrayList<>();
        for (Route route:routeList) {
            RouteDto routeDto = new RouteDto();
            routeDto.setId(route.getId());
            routeDto.setNumber(route.getNumber());
            routeDto.setTimeDto(convertToTimeDto(route.getTimeRoutes()));
            routeDtoList.add(routeDto);
        }
        return routeDtoList;
    }

    public List<TimeDto> convertToTimeDto(List<TimeRoute> timeRouteList){
        List<TimeDto> timeDtoList = new ArrayList<>();
        for (TimeRoute timeRoute: timeRouteList) {
            TimeDto timeDto = new TimeDto();
            timeDto.setName(timeRoute.getName());
            timeDto.setTotal(timeRoute.getTimeTotal());
            timeDto.setObk(timeRoute.getTimeObk());
            timeDto.setFlights(timeRoute.getTimeFlights());
            timeDtoList.add(timeDto);
        }
        return timeDtoList;
    }
    public String convertDateToString(LocalDateTime date){
        return date.getHour() + ":" + date.getMinute();
    }
    public String convertCellToString(Cell cell){
        if(cell != null && !cell.equals("")){
            return cell.toString();
        }
        return null;
    }
}
