package com.schedule.schedule.service.entityService;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
public class ScheduleDto {
    private Date date;
    private List<DepoDto> depoDtos = new ArrayList<>();

}
