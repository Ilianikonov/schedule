package com.schedule.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
@Data
public class FilterDto {
    private LocalDate dateStart; //Дата начала выборки
    private LocalDate dateEnd; //Дата конца выборки
    private Long depo;  //Номер депо (его id)
    private String route; //Номер маршрута (его number).
}
