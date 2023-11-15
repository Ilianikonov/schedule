package com.schedule.schedule.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
public class ScheduleDto {
    private Long id;
    private LocalDate date;
    private List<DepoDto> depoDto = new ArrayList<>();

}
