package com.schedule.schedule.dto;

import lombok.Data;

import java.util.Date;
@Data
public class FilterDto {
    private Date date_start; //Дата начала выборки
    private Date date_end; //Дата конца выборки
    private Long depo;  //Номер депо (его id)
    private String route; //Номер маршрута (его number).
}
