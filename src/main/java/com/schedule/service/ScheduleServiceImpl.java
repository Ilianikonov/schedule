package com.schedule.service;

import com.schedule.dao.ScheduleDao;
import com.schedule.dao.ScheduleRepository;
import com.schedule.dto.*;
import com.schedule.entity.Schedule;
import com.schedule.exception.DublicateException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.NonTransientDataAccessException;
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
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleDao scheduleDao;
    private final ServiceConverter serviceConverter;
    private static String ITOGO_CELL_VALUE = "Итого";
    private static String VSEGO_CELL_VALUE = "Всего";

    @Override
    @Transactional
    public void uploadSchedule(InputStream schedule) throws IOException, DublicateException {
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
        return serviceConverter.convertScheduleToScheduleDto(scheduleRepository.getSchedulesByDate(date));
    }

    public void saveScheduleToDao(Sheet sheet, int shift) throws DublicateException {
        ScheduleDto scheduleDto = buildSchedule(sheet, shift);
        try {
            scheduleRepository.save(serviceConverter.convertScheduleDtoToSchedule(scheduleDto));
        } catch (NonTransientDataAccessException nonTransientDataAccessException){
            throw new DublicateException("Расписание на эту дату уже существует!");
        }

    }

    @Override
    @Transactional
    public List<ScheduleDto> getSchedule(FilterDto filterDto) {
        List<Schedule> scheduleList = scheduleDao.getScheduleList(filterDto);
        return serviceConverter.convertScheduleToScheduleDto(scheduleList);
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
            if (ITOGO_CELL_VALUE.equals(serviceConverter.convertCellToString(sheet.getRow(i).getCell(shift))) && serviceConverter.convertCellToString(sheet.getRow(cellD).getCell(shift)) != null) {
                String cellDepo = serviceConverter.convertCellToString(sheet.getRow(cellD).getCell(shift));
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
                while (!ITOGO_CELL_VALUE.equals(serviceConverter.convertCellToString(sheet.getRow(count).getCell(shift)))) {
                    count++;
                    if (sheet.getRow(count) != null && serviceConverter.convertCellToString(sheet.getRow(count).getCell(shift)) != null && !ITOGO_CELL_VALUE.equals(sheet.getRow(count).getCell(shift)) && !VSEGO_CELL_VALUE.equals(sheet.getRow(count).getCell(shift))) {
                        RouteDto routeDto = new RouteDto();
                        routeDto.setNumber(serviceConverter.convertCellToString(sheet.getRow(count).getCell(shift)));
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
            String cellResult = serviceConverter.convertDateToString(sheet.getRow(4).getCell(i).getLocalDateTimeCellValue());
            timeDto.setName(cellResult);
            timeDtos.add(timeDto);
        }
        TimeDto timeDto = new TimeDto();
        timeDto.setName(sheet.getRow(4).getCell(countTime).toString());
        timeDtos.add(timeDto);
        return timeDtos;
    }
}
