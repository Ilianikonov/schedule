package com.schedule.schedule.service.dtoService;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class ScheduleDto {
    private LocalDateTime date;
    private List<DepoDto> depoDto = new ArrayList<>();

}
