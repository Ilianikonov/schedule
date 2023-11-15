package com.schedule.schedule.controller.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
public class FilterRequest {
    private LocalDate date_start; //Дата начала выборки
    private LocalDate date_end; //Дата конца выборки
    private Long depo;  //Номер депо (его id)
    private String route; //Номер маршрута (его number).
}
