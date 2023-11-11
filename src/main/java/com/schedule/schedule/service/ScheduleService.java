package com.schedule.schedule.service;

import com.schedule.schedule.service.entityService.DepoService;
import com.schedule.schedule.service.entityService.RouteService;
import com.schedule.schedule.service.entityService.ScheduleServiceEntity;
import com.schedule.schedule.service.entityService.TimeService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.asn1.cms.Time;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    public void uploadSchedule(InputStream schedule) throws IOException {
        ScheduleServiceEntity scheduleServiceWeekdays = new ScheduleServiceEntity();
        ScheduleServiceEntity scheduleServiceWeekend = new ScheduleServiceEntity();
        XSSFWorkbook workbook = new XSSFWorkbook(schedule);
        Sheet sheet = workbook.getSheetAt(0);
        scheduleServiceWeekdays.setDate(sheet.getRow(1).getCell(4).getDateCellValue());
        scheduleServiceWeekend.setDate(sheet.getRow(1).getCell(27).getDateCellValue());

//        for (Row row: sheet) {
//            for (Cell cell: row) {
//                System.out.println(cell);
//            }
//        }
        System.out.println(scheduleServiceWeekdays.getDate());
        System.out.println(scheduleServiceWeekend.getDate());
        System.out.println(getDepoServicesOnlyTable(sheet,0));
        System.out.println(getNamesOfTimes(sheet,true).toString());
        System.out.println(getAllRoutesBelongingToDepo(sheet, getDepoServicesOnlyTable(sheet,0).get(3), 0));
        System.out.println(getTotalTimeAndObkForTheRoute(sheet,getAllRoutesBelongingToDepo(sheet, getDepoServicesOnlyTable(sheet,0).get(3), 0).get(1),0));
    }
    private List<TimeService> getNamesOfTimes(Sheet sheet, boolean weekdays){
        List<TimeService> timeServices = new ArrayList<>();
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
        for (int i = addressOrientationColumn; i < countTime; i += 2){
            TimeService timeService = new TimeService();
            String cellResult = sheet.getRow(4).getCell(i).getDateCellValue().toString();
            if (cellResult != null) {
                timeService.setName(cellResult);
                timeServices.add(timeService);
            }
        }
        TimeService timeService = new TimeService();
        timeService.setName(sheet.getRow(4).getCell(countTime).toString());
        timeServices.add(timeService);
        return timeServices;
    }

    private List<TimeService> getTotalTimeAndObkForTheRoute(Sheet sheet, RouteService routeService, int addressOrientationColumn){
        List<TimeService> timeServices = getNamesOfTimes(sheet, true);
        for (Row row: sheet) {
            if (row.getCell(addressOrientationColumn).toString().equals(routeService.getNumber())) {
                int count = addressOrientationColumn;
                for (TimeService timeService: timeServices) {
                    count++;
                    timeService.setTotal(Integer.valueOf((int) row.getCell(count).getNumericCellValue()));
                    count++;
                    timeService.setObk(Integer.valueOf((int) row.getCell(count).getNumericCellValue()));
                }
                timeServices.get(timeServices.size() - 1).setFlights(Integer.valueOf((int) row.getCell( timeServices.size()*2 + 1).getNumericCellValue()));
                return timeServices;
            }
        }
    return null;
    }
    private List<RouteService> getAllRoutesBelongingToDepo(Sheet sheet, DepoService depoServices, int addressOrientationColumn){
        List<RouteService> routeServices = new ArrayList<>();
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
                    if (cell != null) {
                        RouteService routeService = new RouteService();
                        routeService.setNumber(cell);
                        routeServices.add(routeService);
                    }
                }
                return routeServices;
            }
        }
        return null;
    }
    private List<DepoService> getDepoServicesOnlyTable(Sheet sheet, int addressOrientationColumn){
        List<DepoService> depoServices = new ArrayList<>();
        DepoService depoService = new DepoService();
        depoService.setName(sheet.getRow(6).getCell(addressOrientationColumn).toString());
        depoServices.add(depoService);
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
                    DepoService depoService1 = new DepoService();
                    depoService1.setName(cellDepoService);
                    depoServices.add(depoService1);
            }
        }
        return depoServices;
    }
    private List<TimeService> getTimeServices(Sheet sheet){
        return null;
    }
}
