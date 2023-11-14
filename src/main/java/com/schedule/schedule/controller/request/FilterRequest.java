package com.schedule.schedule.controller.request;

import lombok.Data;

import java.util.Date;
@Data
public class FilterRequest {
    private Date date_start; //Дата начала выборки
    private Date date_end; //Дата конца выборки
    private Long depo;  //Номер депо (его id)
    private String route; //Номер маршрута (его number).
}
