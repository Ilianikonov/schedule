package com.schedule.schedule.service.entityService;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
public class ScheduleServiceEntity {
    private Date date;
    private List<DepoService> depoServices = new ArrayList<>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
