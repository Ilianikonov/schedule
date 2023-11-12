package com.schedule.schedule.service;

import com.schedule.schedule.service.entityService.DepoDto;
import com.schedule.schedule.service.entityService.RouteDto;
import com.schedule.schedule.service.entityService.ScheduleDto;
import com.schedule.schedule.service.entityService.TimeDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    public void uploadSchedule(InputStream schedule) throws IOException {
        ScheduleDto scheduleServiceWeekdays = new ScheduleDto();
        ScheduleDto scheduleServiceWeekend = new ScheduleDto();
        XSSFWorkbook workbook = new XSSFWorkbook(schedule);
        Sheet sheet = workbook.getSheetAt(0);
        scheduleServiceWeekdays.setDate(sheet.getRow(1).getCell(4).getDateCellValue());
        scheduleServiceWeekend.setDate(sheet.getRow(1).getCell(27).getDateCellValue());
        scheduleServiceWeekdays.setDepoDtos(getDepoServicesOnlyTable(sheet, 0));
        for (DepoDto depoDto : scheduleServiceWeekdays.getDepoDtos()) {
            depoDto.setRouteDtos(getAllRoutesBelongingToDepo(sheet, depoDto, 0));
            for (RouteDto routeDto: depoDto.getRouteDtos()) {
                routeDto.setTimeDtos(getTotalTimeAndObkForTheRoute(sheet, routeDto, true));
            }
        }
        System.out.println(scheduleServiceWeekdays.getDate());
        System.out.println(scheduleServiceWeekend.getDate());
        System.out.println(getDepoServicesOnlyTable(sheet,0));
        System.out.println(getNamesOfTimes(sheet,true).toString());
        System.out.println(getAllRoutesBelongingToDepo(sheet, getDepoServicesOnlyTable(sheet,0).get(3), 0));
        System.out.println(getTotalTimeAndObkForTheRoute(sheet,getAllRoutesBelongingToDepo(sheet, getDepoServicesOnlyTable(sheet,0).get(3), 0).get(1),true));
        System.out.println(scheduleServiceWeekdays.toString());
    }
    private List<TimeDto> getNamesOfTimes(Sheet sheet, boolean weekdays){
        List<TimeDto> timeDtos = new ArrayList<>();
        int addressOrientationColumn;
        if (weekdays){
            addressOrientationColumn = 1;
        } else {
            addressOrientationColumn = 23;
        }
        int countTime = addressOrientationColumn;
        while (sheet.getRow(4).getCell(countTime + 2) != null){
            countTime++;
        }
        countTime--;
        for (int i = addressOrientationColumn; i < countTime -1 ; i += 2){
            TimeDto timeDto = new TimeDto();
            String cellResult = convertDateToString(sheet.getRow(4).getCell(i).getLocalDateTimeCellValue());
            if (cellResult != null) {
                timeDto.setName(cellResult);
                timeDtos.add(timeDto);
            }
        }
        TimeDto timeDto = new TimeDto();
        timeDto.setName(sheet.getRow(4).getCell(countTime).toString());
        timeDtos.add(timeDto);
        return timeDtos;
    }

    private List<TimeDto> getTotalTimeAndObkForTheRoute(Sheet sheet, RouteDto routeDto, boolean weekdays){
        List<TimeDto> timeDtos = getNamesOfTimes(sheet, true);
        int addressOrientationColumn;
        if (weekdays){
            addressOrientationColumn = 0;
        } else {
            addressOrientationColumn = 23;
        }
        for (Row row: sheet) {
            if (row.getCell(addressOrientationColumn).toString().equals(routeDto.getNumber())) {
                int count = addressOrientationColumn;
                for (TimeDto timeDto : timeDtos) {
                    count++;
                    timeDto.setTotal((int)row.getCell(count).getNumericCellValue());
                    count++;
                    timeDto.setObk((int)row.getCell(count).getNumericCellValue());
                }
                timeDtos.get(timeDtos.size() - 1).setFlights(Integer.valueOf((int) row.getCell( timeDtos.size()*2 + 1).getNumericCellValue()));
                return timeDtos;
            }
        }
    return null;
    }
    private List<RouteDto> getAllRoutesBelongingToDepo(Sheet sheet, DepoDto depoServices, int addressOrientationColumn){
        List<RouteDto> routeDtos = new ArrayList<>();
        for (int i = 6; i <= sheet.getLastRowNum(); i++){
            String cellNameDepo;
            try {
                cellNameDepo = sheet.getRow(i).getCell(addressOrientationColumn).toString();
            } catch (NullPointerException nullPointerException){
                continue;
            }
            if (!cellNameDepo.equals(depoServices.getName())){
                continue;
            } else {
                int count = i;
                while (!sheet.getRow(count).getCell(addressOrientationColumn).toString().equals("Итого")) {
                    count++;
                    String cell = (sheet.getRow(count).getCell(addressOrientationColumn).toString());
                    if (cell != null && !cell.equals("Итого")) {
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
    private List<DepoDto> getDepoServicesOnlyTable(Sheet sheet, int addressOrientationColumn){
        List<DepoDto> depoDtos = new ArrayList<>();
        DepoDto depoDto = new DepoDto();
        depoDto.setName(sheet.getRow(6).getCell(addressOrientationColumn).toString());
        depoDtos.add(depoDto);
        for (int i = 5; i <= sheet.getLastRowNum(); i++){
            String cell1ToRow;
            String cellDepoService;
            String cellEnd;
            try {
                cell1ToRow = sheet.getRow(i).getCell(addressOrientationColumn).toString();
                cellDepoService = sheet.getRow(i + 1).getCell(addressOrientationColumn).toString();
                cellEnd = sheet.getRow(i + 2).getCell(addressOrientationColumn).toString();
            } catch (NullPointerException nullPointerException){
                break;
            }
            if (cell1ToRow != null && cellDepoService != null && cell1ToRow.equals("Итого") && !cellEnd.equals("Всего")){
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
}
