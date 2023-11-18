package com.schedule.controller.response;

import lombok.Data;

import static java.lang.Double.isNaN;

@Data
public class RouteResponse {
    private long id;
    private String number;

    public void setNumber(String number) {
        try {
            double i = Double.parseDouble(number);
            this.number = Integer.toString((int)i);
        } catch (NumberFormatException numberFormatException){
            this.number = number;
        }
    }
}
