package com.schedule.controller.request;

import lombok.Data;

import java.time.LocalDate;
@Data
public class FilterRequest {
    private LocalDate date_start; //Дата начала выборки
    private LocalDate date_end; //Дата конца выборки
    private Long depo;  //Номер депо (его id)
    private Object route; //Номер маршрута (его number).

    public void setRoute(Object route) {
        try {
            this.route = Double.valueOf((String) route);
        } catch (NumberFormatException e) {
            this.route = route.toString();
        }
    }
}
