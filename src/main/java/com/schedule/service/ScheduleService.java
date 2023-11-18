package com.schedule.service;

import com.schedule.dao.ScheduleRepository;
import com.schedule.dto.*;
import com.schedule.entity.Depo;
import com.schedule.entity.Route;
import com.schedule.entity.Schedule;
import com.schedule.exception.FilterFaultException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService implements ScheduleServiceInter {
    private final ScheduleRepository scheduleRepository;
    private final EntityManager entityManager;
    private final Convert convert;
    private static String ITOGO_CELL_VALUE = "Итого";
    private static String VSEGO_CELL_VALUE = "Всего";

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
    public List<ScheduleDto> getCurrentSchedule() {
        LocalDate date = LocalDateTime.now().toLocalDate();
        return convert.convertScheduleToScheduleDto(scheduleRepository.getSchedulesByDate(date));
    }

    @Transactional
    public void saveScheduleToDao(Sheet sheet, int shift){
        ScheduleDto scheduleDto = buildSchedule(sheet, shift);
        scheduleRepository.save(convert.convertScheduleDtoToSchedule(scheduleDto));
    }

    @Override
    @Transactional
    public List<ScheduleDto> getSchedule(FilterDto filterDto) {
        if (filterDto.getDateStart() == (null) && filterDto.getDateEnd() == null && filterDto.getDepo() == null && filterDto.getRoute() == null){
            throw new FilterFaultException("Фильтр пуст, укажите хотя бы один параметр");
        } else if (filterDto.getDateEnd() == null){
            filterDto.setDateEnd(LocalDate.now());
        }
        LocalDate filterDateStart = filterDto.getDateStart();
        LocalDate filterDateEnd = filterDto.getDateEnd();
        Long filterDepoId = filterDto.getDepo();
        String filterRouteNumber = filterDto.getRoute();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Schedule> queryScnedule = cb.createQuery(Schedule.class);
        Root<Schedule> rootSchedule = queryScnedule.from(Schedule.class);
        queryScnedule.select(rootSchedule);
        List<Predicate> predicates = new ArrayList<>();
        if (filterDateEnd != null && filterDateStart != null) {
            predicates.add(cb.between(rootSchedule.get("date"), filterDateStart, filterDateEnd));
        }
        if (filterDateStart != null && filterDateEnd == null) {
            predicates.add(cb.greaterThanOrEqualTo(rootSchedule.get("date"), filterDateStart));
        }
        if (filterDateEnd != null && filterDateStart == null) {
            predicates.add(cb.lessThanOrEqualTo(rootSchedule.get("date"), filterDateEnd));
        }
        if (!predicates.isEmpty()) {
            queryScnedule.where(predicates.toArray(new Predicate[0]));
        }
        List<Schedule> scheduleList = entityManager.createQuery(queryScnedule).getResultList();
        if(filterDepoId != null){
            for (Schedule schedule: scheduleList) {
                List<Depo> filteredDepoList = new ArrayList<>();
                for (Depo depo: schedule.getDepoList()) {
                    if (depo.getId() == filterDepoId){
                        filteredDepoList.add(depo);
                    }
                }
                schedule.setDepoList(filteredDepoList);
            }
        }
        if (filterRouteNumber != null){
            for (Schedule schedule:scheduleList) {
                List<Depo> itogListDepo = new ArrayList<>();
                for (Depo depo: schedule.getDepoList()) {
                    List<Route> filtreRouteList = new ArrayList<>();
                    boolean indecator = false;
                    for (Route route:depo.getRoute()) {
                        if(route.getNumber().equals(filterRouteNumber)){
                            filtreRouteList.add(route);
                            indecator = true;
                        }
                    }
                    if(indecator) {
                        depo.setRoute(filtreRouteList);
                        itogListDepo.add(depo);
                    }
                }
                schedule.setDepoList(itogListDepo);
            }
        }
        return convert.convertScheduleToScheduleDto(scheduleList);
    }

    private ScheduleDto buildSchedule(Sheet sheet, int shift) {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setDate(getDateSchedule(sheet, shift));
        scheduleDto.setDepoDto(getDepoOnlyTable(sheet,shift));
        return scheduleDto;
    }

    private LocalDate getDateSchedule(Sheet sheet, int shift){
        return sheet.getRow(1).getCell(shift + 4).getLocalDateTimeCellValue().toLocalDate();
    }

    private List<DepoDto> getDepoOnlyTable(Sheet sheet, int shift){
        List<DepoDto> depoDtos = new ArrayList<>();
        DepoDto depoDto = new DepoDto();
        depoDto.setName(sheet.getRow(6).getCell(shift).toString());
        depoDto.setRouteDto(getAllRoutesBelongingToDepo(sheet, depoDto, shift));
        depoDtos.add(depoDto);
        for (int i = 7; i < sheet.getLastRowNum(); i++) {
            int cellD = 1;
            cellD += i;
            if (ITOGO_CELL_VALUE.equals(convert.convertCellToString(sheet.getRow(i).getCell(shift))) && convert.convertCellToString(sheet.getRow(cellD).getCell(shift)) != null) {
                String cellDepo = convert.convertCellToString(sheet.getRow(cellD).getCell(shift));
                if (sheet.getRow(cellD) != null && sheet.getRow(cellD).getCell(shift).getStringCellValue() != null && !VSEGO_CELL_VALUE.equals(sheet.getRow(cellD + 1).getCell(shift).toString())) {
                    DepoDto depoDto1 = new DepoDto();
                    depoDto1.setName(cellDepo);
                    depoDto1.setRouteDto(getAllRoutesBelongingToDepo(sheet, depoDto1, shift));
                    depoDtos.add(depoDto1);
                }
            }
        }
        return depoDtos;
    }

    private List<RouteDto> getAllRoutesBelongingToDepo(Sheet sheet, DepoDto depoDto, int shift){
        List<RouteDto> routeDtos = new ArrayList<>();
        for (int i = 6; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i).getCell(shift) != null && sheet.getRow(i).getCell(shift).toString().equals(depoDto.getName())) {
                int count = i;
                while (!ITOGO_CELL_VALUE.equals(convert.convertCellToString(sheet.getRow(count).getCell(shift)))) {
                    count++;
                    if (sheet.getRow(count) != null && convert.convertCellToString(sheet.getRow(count).getCell(shift)) != null && !ITOGO_CELL_VALUE.equals(sheet.getRow(count).getCell(shift)) && !VSEGO_CELL_VALUE.equals(sheet.getRow(count).getCell(shift))) {
                        RouteDto routeDto = new RouteDto();
                        routeDto.setNumber(convert.convertCellToString(sheet.getRow(count).getCell(shift)));
                        List<TimeDto> timeDtos = getNamesOfTimes(sheet, shift);
                        int countTime = 0;
                        for (int y = 1; y <= timeDtos.size() * 2; y++) {
                            int time = 0;
                            if(sheet.getRow(count).getCell(shift + y) != null){
                                time = (int)sheet.getRow(count).getCell(shift + y).getNumericCellValue();
                            }
                            if (y % 2 != 0) {
                                timeDtos.get(countTime).setTotal(time);
                            } else {
                                timeDtos.get(countTime).setObk(time);
                                countTime++;
                            }
                        }
                        if(sheet.getRow(count).getCell(timeDtos.size() * 2 + 1 + shift) == null){
                            timeDtos.get(timeDtos.size() - 1).setFlights(0);
                        } else {
                            timeDtos.get(timeDtos.size() - 1).setFlights(((int) sheet.getRow(count).getCell(timeDtos.size() * 2 + 1 + shift).getNumericCellValue()));
                        }
                        routeDto.setTimeDto(timeDtos);
                        if(routeDto.getNumber() != null && !routeDto.getNumber().equals("") && !ITOGO_CELL_VALUE.equals(routeDto.getNumber()) && !VSEGO_CELL_VALUE.equals(routeDto.getNumber())){
                            routeDtos.add(routeDto);
                        }
                    }
                    int r = count;
                    if (sheet.getRow(count) == null){
                        count++;
                    }
                    if (sheet.getRow(r) == null && sheet.getRow(r + 1) == null){
                        return routeDtos;
                    }
                }
            }
        }
        return routeDtos;
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
            String cellResult = convert.convertDateToString(sheet.getRow(4).getCell(i).getLocalDateTimeCellValue());
            timeDto.setName(cellResult);
            timeDtos.add(timeDto);
        }
        TimeDto timeDto = new TimeDto();
        timeDto.setName(sheet.getRow(4).getCell(countTime).toString());
        timeDtos.add(timeDto);
        return timeDtos;
    }
}
