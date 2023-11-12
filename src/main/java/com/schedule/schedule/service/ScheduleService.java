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
    private static String ITOGO_CELL_VALUE = "Итого";
    private static String VSEGO_CELL_VALUE = "Всего";
    public void uploadSchedule(InputStream schedule) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(schedule);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println(buildWeekendSchedule(sheet, 0));
//        System.out.println(getDepoServicesOnlyTable(sheet,0));
//        System.out.println(getNamesOfTimes(sheet,sheet).toString());
//        System.out.println(getAllRoutesBelongingToDepo(sheet, getDepoServicesOnlyTable(sheet,0).get(3), 0));
        System.out.println(getTotalTimeAndObkForTheRoute(sheet,getAllRoutesBelongingToDepo(sheet, getDepoServicesOnlyTable(sheet,0).get(3), 0).get(1),0));

    }

    private LocalDateTime getDateSchedule(Sheet sheet, int shift){
        return sheet.getRow(1).getCell(shift + 3).getLocalDateTimeCellValue();
    }

    private ScheduleDto buildWeekendSchedule(Sheet sheet, int shift) {
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
        for (int x = shift + 7; x <= sheet.getLastRowNum(); x++) {
            if (sheet.getRow(x).getCell(shift).toString().equals(routeDto.getNumber())) {
                int count = 0;
                for (int i = 1; i < timeDtos.size() * 2; i++) {
                    if (i % 2 != 0){
                        timeDtos.get(count).setTotal((int)sheet.getRow(x).getCell(i).getNumericCellValue());
                    } else {
                        timeDtos.get(count).setObk((int)sheet.getRow(x).getCell(i).getNumericCellValue());
                        count++;
                    }

                }
                timeDtos.get(timeDtos.size() - 1).setFlights(((int) sheet.getRow(x).getCell( timeDtos.size()*2 + 1).getNumericCellValue()));
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
            String cellEnd;
            if (convertCellToString(sheet.getRow(i).getCell(shift)) == null &&  convertCellToString(sheet.getRow(i + 1).getCell(shift)) == null){
                continue;
            }
            cell1ToRow = convertCellToString(sheet.getRow(i).getCell(shift));
            cellDepoService = convertCellToString(sheet.getRow(i + 1).getCell(shift));
            cellEnd = convertCellToString(sheet.getRow(i + 2).getCell(shift));
            if (cellDepoService != null && cell1ToRow.equals(ITOGO_CELL_VALUE)){
                DepoDto depoDto1 = new DepoDto();
                depoDto1.setName(cellDepoService);
                depoDtos.add(depoDto1);
            } else if (cellEnd != null && cellEnd.equals(VSEGO_CELL_VALUE)){
                return depoDtos;
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
        return null;
    }
}
