package com.schedule.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedule.controller.request.FilterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScheduleControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static final String GET_SCHEDULE_ACTUAL = "/getScheduleActual";
    private static final String GET_SCHEDULE = "/getSchedule";

    @Test
    void getCurrentSchedule() throws Exception {
        LocalDate dateStart = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_start(dateStart);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE_ACTUAL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
             Assertions.assertTrue((dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() == dateStart.getDayOfYear()));
        }

    }

    /**
     * Если указано только date_start - выдать общее количество рейсов по всем депо
     * и по всем маршрутам с указанной даты по текущий день.
     * @throws Exception - когда фильтер пуст
     */
    @Test
    void getScheduleTest1() throws Exception {
        LocalDate dateStart = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_start(dateStart);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            if (dateResult.getYear() > dateStart.getYear()) {
                Assertions.assertTrue(true);
            } else Assertions.assertTrue((dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()));
        }
    }

    //Если указано только date_end - выдать общее количество рейсов по всем депо и по всем
    // маршрутам с даты самой первой записи (с начала ведения расписания) по указанную дату
    @Test
    void getScheduleTest2() throws Exception {
        LocalDate dateEnd = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_end(dateEnd);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            if (dateResult.getYear() < dateEnd.getYear()) {
                Assertions.assertTrue(true);
            } else Assertions.assertTrue(dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear());
        }
    }

    // Если указано только depo - выдать общее количество рейсов
    // по указанному депо по всем маршрутам за все время
    @Test
    void getScheduleTest3() throws Exception {
        long depo = 1;
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDepo(depo);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
                Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()),depo);
        }
    }

    // Если указано только route - выдать общее количество рейсов
    // по всем депо по указанному маршруту за все время
    @Test
    void getScheduleTest4() throws Exception {
        String route = "3";
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"),route);
        }
    }

    // Если указано только date_start и date_end - выдать общее количество рейсов
    // по всем депо по всем маршрутам за указанное время
    @Test
    void getScheduleTest5() throws Exception {
        LocalDate dateStart = LocalDate.of(2023,11,9);
        LocalDate dateEnd = LocalDate.of(2023,11,10);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_start(dateStart);
        filterRequest.setDate_end(dateEnd);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            if (dateResult.getYear() > dateStart.getYear() && dateResult.getDayOfYear() < dateEnd.getDayOfYear()) {
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()){
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear()){
                Assertions.assertTrue(true);
            } else {
                Assertions.fail();
            }
        }
    }

    // Если указано только date_start и depo - выдать общее количество рейсов по указанному
    // депо по всем маршрутам с указанной даты по текущий день
    @Test
    void getScheduleTest6() throws Exception {
        LocalDate dateStart = LocalDate.of(2023,11,5);
        long depo = 4;
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_start(dateStart);
        filterRequest.setDepo(depo);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
            Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()),depo);
            if (dateResult.getYear() > dateStart.getYear()) {
                Assertions.assertTrue(true);
            } else Assertions.assertTrue((dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()));
        }
    }

    // Если указано только date_start и route - выдать общее количество рейсов
    // по всем депо и по указанному маршруту с указанной даты по текущий день
    @Test
    void getScheduleTest7() throws Exception {
        LocalDate dateStart = LocalDate.of(2023,11,5);
        String route = "4";
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_start(dateStart);
        filterRequest.setRoute(route);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"),route);
            if (dateResult.getYear() > dateStart.getYear()) {
                Assertions.assertTrue(true);
            } else Assertions.assertTrue((dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()));
        }
    }

    // Если указано только depo и route - выдать общее количество рейсов
    // по указанному депо и по указанному маршруту за все время
    @Test
    void getScheduleTest8() throws Exception {
        long depo = 1;
            String route = "3";
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDepo(depo);
        filterRequest.setRoute(route);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String,Object> map: result) {
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"),route);
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
            Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()),depo);
        }
    }

    // Если указано только date_ end и depo - выдать общее количество
    // рейсов по указанному депо по всем маршрутам с начала по указанную дату день.
    @Test
    void getScheduleTest9() throws Exception {
        long depo = 1;
        LocalDate dateEnd = LocalDate.of(2023, 11, 7);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDepo(depo);
        filterRequest.setDate_end(dateEnd);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {
        });
        for (Map<String, Object> map : result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
            Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()), depo);
            if (dateResult.getYear() < dateEnd.getYear()) {
                Assertions.assertTrue(true);
            } else
                Assertions.assertTrue((dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear()));

        }
    }

    // Если указано только date_ end и route - выдать общее количество рейсов
    // по всем депо по указанному маршруту с начала по указанную дату день
    @Test
    void getScheduleTest10() throws Exception {
        String route = "1";
        LocalDate dateEnd = LocalDate.of(2023,11,7);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        filterRequest.setDate_end(dateEnd);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String, Object> map : result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"),route);
            if (dateResult.getYear() < dateEnd.getYear()) {
                Assertions.assertTrue(true);
            } else
                Assertions.assertTrue((dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear()));

        }
    }

    //	Если указано только date_start, date_ end и depo - выдать общее количество рейсов
    //	по указанному депо по всем маршрутам за указанное время
    @Test
    void getScheduleTest11() throws Exception {
        long depo = 1;
        LocalDate dateStart = LocalDate.of(2023,11,3);
        LocalDate dateEnd = LocalDate.of(2023,11,7);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDepo(depo);
        filterRequest.setDate_start(dateStart);
        filterRequest.setDate_end(dateEnd);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String, Object> map : result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
            Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()), depo);
            if (dateResult.getYear() > dateStart.getYear() && dateResult.getDayOfYear() < dateEnd.getDayOfYear()) {
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()){
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear()){
                Assertions.assertTrue(true);
            } else {
                Assertions.fail();
            }
        }
    }

    // Если указано только date_start, date_ end и route - выдать
    // общее количество рейсов по всем депо по указанному маршруту за указанное время
    @Test
    void getScheduleTest12() throws Exception {
        String route = "1";
        LocalDate dateStart = LocalDate.of(2023,11,3);
        LocalDate dateEnd = LocalDate.of(2023,11,7);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        filterRequest.setDate_start(dateStart);
        filterRequest.setDate_end(dateEnd);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String, Object> map : result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"),route);
            if (dateResult.getYear() > dateStart.getYear() && dateResult.getDayOfYear() < dateEnd.getDayOfYear()) {
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()){
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear()){
                Assertions.assertTrue(true);
            } else {
                Assertions.fail();
            }
        }
    }

    // Если указано только date_start, depo и route - выдать общее количество рейсов
    // по указанному депо по указанному маршруту с указанной даты по текущий день.
    @Test
    void getScheduleTest13() throws Exception {
        String route = "3";
        long depo = 1;
        LocalDate dateStart = LocalDate.of(2023, 11, 5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        filterRequest.setDate_start(dateStart);
        filterRequest.setDepo(depo);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String, Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String, Object> map : result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
            Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()), depo);
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"), route);
            if (dateResult.getYear() > dateStart.getYear()) {
                Assertions.assertTrue(true);
            } else
                Assertions.assertTrue((dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()));
        }
    }

   //	Если указано только date_ end, depo и route - выдать общее количество рейсов
   //	по указанному депо по указанному маршруту с даты самой первой записи
   //	(с начала ведения расписания) по указанную дату
    @Test
    void getScheduleTest14() throws Exception {
        String route = "3";
        long depo = 1;
        LocalDate dateEnd = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        filterRequest.setDate_end(dateEnd);
        filterRequest.setDepo(depo);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String, Object> map : result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
            Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()), depo);
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"), route);
            if (dateResult.getYear() < dateEnd.getYear()) {
                Assertions.assertTrue(true);
            } else{
                Assertions.assertTrue((dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear()));
            }
        }
    }

    // Если указаны все параметры - выдать общее количество рейсов по указанному
    // депо по указанному маршруту за указанное время
    @Test
    void getScheduleTest15() throws Exception {
        String route = "3";
        long depo = 1;
        LocalDate dateStart = LocalDate.of(2023,11,2);
        LocalDate dateEnd = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        filterRequest.setDate_end(dateEnd);
        filterRequest.setDepo(depo);
        filterRequest.setDate_start(dateStart);
        String jsonResult = mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Map<String,Object>> result = objectMapper.readValue(jsonResult, new TypeReference<>() {});
        for (Map<String, Object> map : result) {
            LocalDate dateResult = LocalDate.parse(map.get("date").toString());
            LinkedHashMap<String, Object> depoMapResult = (LinkedHashMap<String, Object>) map.get("depo");
            Assertions.assertEquals(Long.valueOf(depoMapResult.get("id").toString()), depo);
            LinkedHashMap<String, Object> routeMapResult = (LinkedHashMap<String, Object>) map.get("route");
            Assertions.assertEquals(routeMapResult.get("number"),route);
            if (dateResult.getYear() > dateStart.getYear() && dateResult.getDayOfYear() < dateEnd.getDayOfYear()) {
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateStart.getYear() && dateResult.getDayOfYear() >= dateStart.getDayOfYear()){
                Assertions.assertTrue(true);
            } else if(dateResult.getYear() == dateEnd.getYear() && dateResult.getDayOfYear() <= dateEnd.getDayOfYear()){
                Assertions.assertTrue(true);
            } else {
                Assertions.fail();
            }
        }
    }
}
