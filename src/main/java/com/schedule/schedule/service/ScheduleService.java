package com.schedule.schedule.service;

import com.schedule.schedule.dao.ScheduleRepository;
import com.schedule.schedule.dto.*;
import com.schedule.schedule.entity.Depo;
import com.schedule.schedule.entity.Route;
import com.schedule.schedule.entity.Schedule;
import com.schedule.schedule.entity.TimeRoute;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService implements ScheduleServiceInter {
    private final ScheduleRepository scheduleRepository;
    private final EntityManager entityManager;
    private static String ITOGO_CELL_VALUE = "Итого";

    @Override
    public void uploadSchedule(InputStream schedule) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(schedule);
        Sheet sheet = workbook.getSheetAt(0);
        int shiftWeekdays = 0;
        int shiftWeekend = 23;

        saveScheduleToDao(sheet, shiftWeekdays);
        saveScheduleToDao(sheet, shiftWeekend);
    }

    @Override
    public List<ScheduleDto> getSchedule(FilterDto filterDto) throws ParseException {

        LocalDate filterDateStart = filterDto.getDateStart();
        LocalDate filterDateEnd = filterDto.getDateEnd();
        Long filterDepoId = filterDto.getDepo();
        String filterRouteNumber = filterDto.getRoute();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Schedule> query = cb.createQuery(Schedule.class);
        Root<Schedule> root = query.from(Schedule.class);


        List<Predicate> predicates = new ArrayList<>();

        if (filterDepoId != null) {
            predicates.add(cb.equal(root.join("depo.id"), filterDepoId));
        }
        if (filterDateEnd != null && filterDateStart != null) {
            predicates.add(cb.between(root.get("date"), filterDateStart, filterDateEnd));
        }
        if (filterDateStart != null && filterDateEnd == null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), filterDateStart));
        }
        if (filterDateEnd != null && filterDateStart == null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), filterDateEnd));
        }
        if (filterRouteNumber != null) {
            predicates.add(cb.equal(root.join("number"), filterRouteNumber));
        }
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        return convertScheduleToScheduleDto(entityManager.createQuery(query).getResultList());
    }

    @Override
    public List<ScheduleDto> getCurrentSchedule() throws ParseException {
        LocalDate date = LocalDateTime.now().toLocalDate();
        LocalDate date1 = LocalDate.of(2023,11,4);
        return convertScheduleToScheduleDto(scheduleRepository.getSchedulesByDate(date1));
    }
    private List<ScheduleDto> convertScheduleToScheduleDto(List<Schedule> scheduleList) throws ParseException {
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
    private List<DepoDto> convertToDepoDpo(List<Depo> depoList){
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

    private List<RouteDto> convertToRouteDto(List<Route> routeList){
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

    private List<TimeDto> convertToTimeDto(List<TimeRoute> timeRouteList){
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

    @Transactional
    public void saveScheduleToDao(Sheet sheet, int shift){
        ScheduleDto scheduleDto = buildSchedule(sheet, shift);
        scheduleRepository.save(convertScheduleDtoToSchedule(scheduleDto));
    }
    private Schedule convertScheduleDtoToSchedule(ScheduleDto scheduleDto){
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDto.getDate());
        schedule.setDepoList(convertToDepo(scheduleDto.getDepoDto(), schedule));
        return schedule;
    }
    private List<Depo> convertToDepo(List<DepoDto> depoDtoList, Schedule schedule){
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
    private List<Route> convertToRoute(List<RouteDto> routeDtoList, Depo depo){
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
    private List<TimeRoute> convertToTimeRoute(List<TimeDto> timeDtoList, Route route){
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

    private LocalDate getDateSchedule(Sheet sheet, int shift){
        return sheet.getRow(1).getCell(shift + 4).getLocalDateTimeCellValue().toLocalDate();
    }

    private ScheduleDto buildSchedule(Sheet sheet, int shift) {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setDate(getDateSchedule(sheet, shift));
        scheduleDto.setDepoDto(getDepoServicesOnlyTable(sheet, shift));
        for (DepoDto depoDto: scheduleDto.getDepoDto()) {
            depoDto.setRouteDto(getAllRoutesBelongingToDepo(sheet, depoDto, shift));
        }
        for (DepoDto depoDto: scheduleDto.getDepoDto()) {
            for (RouteDto routeDto: depoDto.getRouteDto()) {
                routeDto.setTimeDto(getTotalTimeAndObkForTheRoute(sheet, routeDto, shift));
            }
        }
        return scheduleDto;
    }

    private List<TimeDto> getNamesOfTimes(Sheet sheet, int shift){
        List<TimeDto> timeDtos = new ArrayList<>();
        int addressOrientationColumn = shift + 1;
        int countTime = addressOrientationColumn;
        while (sheet.getRow(4).getCell(countTime + 2) != null){
            countTime++;
        }
        countTime--;
        for (int i = addressOrientationColumn; i < countTime -1 ; i += 2){
            TimeDto timeDto = new TimeDto();
            String cellResult = convertDateToString(sheet.getRow(4).getCell(i).getLocalDateTimeCellValue());
            timeDto.setName(cellResult);
            timeDtos.add(timeDto);
        }
        TimeDto timeDto = new TimeDto();
        timeDto.setName(sheet.getRow(4).getCell(countTime).toString());
        timeDtos.add(timeDto);
        return timeDtos;
    }

    private List<TimeDto> getTotalTimeAndObkForTheRoute(Sheet sheet, RouteDto routeDto, int shift){
        List<TimeDto> timeDtos = getNamesOfTimes(sheet, shift);
        for (int x = 7; x <= sheet.getLastRowNum(); x++) {
            if (sheet.getRow(x).getCell(shift).toString().equals(routeDto.getNumber())) {
                int count = 0;
                for (int i = 1; i <= timeDtos.size() * 2; i++) {
                    if (i % 2 != 0){
                        timeDtos.get(count).setTotal((int)sheet.getRow(x).getCell(shift + i).getNumericCellValue());
                    } else {
                        timeDtos.get(count).setObk((int)sheet.getRow(x).getCell(shift + i).getNumericCellValue());
                        count++;
                    }

                }
                timeDtos.get(timeDtos.size() - 1).setFlights(((int) sheet.getRow(x).getCell( timeDtos.size()*2 + 1 + shift).getNumericCellValue()));
                return timeDtos;
            }
        }
    return null;
    }
    private List<RouteDto> getAllRoutesBelongingToDepo(Sheet sheet, DepoDto depoServices, int addressOrientationColumn){
        List<RouteDto> routeDtos = new ArrayList<>();
        for (int i = 6; i <= sheet.getLastRowNum(); i++){
            String cellNameDepo = sheet.getRow(i).getCell(addressOrientationColumn).toString();
            if (cellNameDepo != null && cellNameDepo.equals(depoServices.getName())){
                int count = i;
                while (!sheet.getRow(count).getCell(addressOrientationColumn).toString().equals(ITOGO_CELL_VALUE)) {
                    count++;
                    String cell = (sheet.getRow(count).getCell(addressOrientationColumn).toString());
                    if (cell != null && !cell.equals(ITOGO_CELL_VALUE)) {
                        RouteDto routeDto = new RouteDto();
                        routeDto.setNumber(cell);
                        routeDtos.add(routeDto);
                    }
                }
                return routeDtos;
            }
        }
        return null;
    }
    private List<DepoDto> getDepoServicesOnlyTable(Sheet sheet, int shift){
        List<DepoDto> depoDtos = new ArrayList<>();
        DepoDto depoDto = new DepoDto();
        depoDto.setName(sheet.getRow(6).getCell(shift).toString());
        depoDtos.add(depoDto);
        for (int i = 7; i < sheet.getLastRowNum(); i++){
            String cell1ToRow;
            String cellDepoService;
            if (convertCellToString(sheet.getRow(i).getCell(shift)) == null &&  convertCellToString(sheet.getRow(i + 1).getCell(shift)) == null){
                continue;
            }
            cell1ToRow = convertCellToString(sheet.getRow(i).getCell(shift));
            cellDepoService = convertCellToString(sheet.getRow(i + 1).getCell(shift));
            if (cellDepoService != null && cell1ToRow.equals(ITOGO_CELL_VALUE)) {
                DepoDto depoDto1 = new DepoDto();
                depoDto1.setName(cellDepoService);
                depoDtos.add(depoDto1);
            }
        }
        return depoDtos;
    }

    private String convertDateToString(LocalDateTime date){
        return date.getHour() + ":" + date.getMinute();
    }
    private String convertCellToString(Cell cell){
        if(cell != null){
            return cell.toString();
        }
        return "";
    }
}
