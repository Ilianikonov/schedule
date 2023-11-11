package com.schedule.schedule.service;

import com.schedule.schedule.service.entityService.DepoService;
import com.schedule.schedule.service.entityService.ScheduleServiceEntity;
import com.schedule.schedule.service.entityService.TimeService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
        System.out.println(getDepoService(sheet));
    }
    private List<DepoService> getDepoService(Sheet sheet){
        List<DepoService> depoServices = new ArrayList<>();
        DepoService depoService = new DepoService();
        depoService.setName(sheet.getRow(6).getCell(0).toString());
        depoServices.add(depoService);
        for (int i = 5; i <= sheet.getLastRowNum(); i++){
            String cell1ToRow;
            String cellDepoService;
            String cellEnd;
            try {
                cell1ToRow = sheet.getRow(i).getCell(0).toString();
                cellDepoService = sheet.getRow(i + 1).getCell(0).toString();
                cellEnd = sheet.getRow(i + 2).getCell(0).toString();
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
