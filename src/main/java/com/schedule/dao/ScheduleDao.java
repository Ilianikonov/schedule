package com.schedule.dao;

import com.schedule.dto.FilterDto;
import com.schedule.entity.Depo;
import com.schedule.entity.Route;
import com.schedule.entity.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ScheduleDao {
    private final EntityManager entityManager;

    public List<Schedule> getScheduleList(FilterDto filterDto) {
        LocalDate filterDateStart = filterDto.getDateStart();
        LocalDate filterDateEnd = filterDto.getDateEnd();
        Long filterDepoId = filterDto.getDepo();
        String filterRouteNumber = filterDto.getRoute();

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Schedule> queryScnedule = cb.createQuery(Schedule.class);
        Root<Schedule> rootSchedule = queryScnedule.from(Schedule.class);

        List<Predicate> predicates = new ArrayList<>();
        if (filterDateEnd != null && filterDateStart != null) {
            predicates.add(cb.between(rootSchedule.get("date"), filterDateStart, filterDateEnd));
        }
        if (filterDateStart != null && filterDateEnd == null) {
            predicates.add(cb.greaterThanOrEqualTo(rootSchedule.get("date"), filterDateStart));
        }
        if (filterDateEnd != null && filterDateStart == null) {
            predicates.add(cb.lessThanOrEqualTo(rootSchedule.get("date"), filterDateEnd));
        }
        if (!predicates.isEmpty()) {
            queryScnedule.where(predicates.toArray(new Predicate[0]));
        }

        List<Schedule> scheduleList = entityManager.createQuery(queryScnedule).getResultList();

        if(filterDepoId != null){
            for (Schedule schedule: scheduleList) {
                List<Depo> filteredDepoList = new ArrayList<>();
                for (Depo depo: schedule.getDepoList()) {
                    if (depo.getId() == filterDepoId){
                        filteredDepoList.add(depo);
                    }
                }
                schedule.setDepoList(filteredDepoList);
            }
        }
        if (filterRouteNumber != null){
            for (Schedule schedule:scheduleList) {
                List<Depo> itogListDepo = new ArrayList<>();
                for (Depo depo: schedule.getDepoList()) {
                    List<Route> filtreRouteList = new ArrayList<>();
                    boolean indecator = false;
                    for (Route route:depo.getRoute()) {
                        if(route.getNumber().equals(filterRouteNumber)){
                            filtreRouteList.add(route);
                            indecator = true;
                        }
                    }
                    if(indecator) {
                        depo.setRoute(filtreRouteList);
                        itogListDepo.add(depo);
                    }
                }
                schedule.setDepoList(itogListDepo);
            }
        }
        return scheduleList;
    }
}
