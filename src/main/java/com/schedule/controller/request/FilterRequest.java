package com.schedule.controller.request;

import lombok.Data;

import java.time.LocalDate;
@Data
public class FilterRequest {
    private LocalDate date_start; //Дата начала выборки
    private LocalDate date_end; //Дата конца выборки
    private String depo;  //название депо (его name)
    private String route; //Номер маршрута (его number).
}
