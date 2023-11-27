package com.schedule.controller.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ScheduleResponse {
    private Long id;
    private LocalDate date;
    private List<DepoResponse> depoResponses = new ArrayList<>();
}
