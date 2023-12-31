package com.schedule.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class FilterDto {
    private LocalDate dateStart; //Дата начала выборки
    private LocalDate dateEnd; //Дата конца выборки
    private String depo;  //Номер депо (его name)
    private String route; //Номер маршрута (его number).
}
