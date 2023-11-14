package com.schedule.schedule.dto;

import com.schedule.schedule.dto.DepoDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Data
public class ScheduleDto {
    private Long id;
    private LocalDateTime date;
    private List<DepoDto> depoDto = new ArrayList<>();

}
