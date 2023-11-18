package com.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedule.controller.request.FilterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScheduleApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private static String GET_SCHEDULE_ACTUAL = "/getScheduleActual";
    private static String GET_SCHEDULE = "/getSchedule";

    @Test
    void getCurrentSchedule(){

    }

    //Если указано только date_start - выдать общее количество рейсов по всем депо
    // и по всем маршрутам с указанной даты по текущий день.
    @Test
    void getScheduleTest1() throws Exception {
        LocalDate dateStart = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_start(dateStart);
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,8)));
    }

    //Если указано только date_end - выдать общее количество рейсов по всем депо и по всем
    // маршрутам с даты самой первой записи (с начала ведения расписания) по указанную дату
    @Test
    void getScheduleTest2() throws Exception {
        LocalDate dateEnd = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_end(dateEnd);
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,4)));
    }

    // Если указано только depo - выдать общее количество рейсов
    // по указанному депо по всем маршрутам за все время
    @Test
    void getScheduleTest3() throws Exception {
        long depo = 1;
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDepo(depo);
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depo").value(depo));
    }

    // Если указано только route - выдать общее количество рейсов
    // по всем депо по указанному маршруту за все время
    @Test
    void getScheduleTest4() throws Exception {
        String route = "3";
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(route));
    }

    // Если указано только date_start и date_end - выдать общее количество рейсов
    // по всем депо по всем маршрутам за указанное время
    @Test
    void getScheduleTest5() throws Exception {
        LocalDate dateStart = LocalDate.of(2023,11,5);
        LocalDate dateEnd = LocalDate.of(2023,11,10);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDate_start(dateStart);
        filterRequest.setDate_end(dateEnd);
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,8)));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,8)))
                .andExpect(jsonPath("$.depo").value(depo));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,8)))
                .andExpect(jsonPath("$.number").value(route));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depo").value(depo))
                .andExpect(jsonPath("$.number").value(route));
    }

    // Если указано только date_ end и depo - выдать общее количество
    // рейсов по указанному депо по всем маршрутам с начала по указанную дату день.
    @Test
    void getScheduleTest9() throws Exception {
        long depo = 1;
        LocalDate dateEnd = LocalDate.of(2023,11,7);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setDepo(depo);
        filterRequest.setDate_end(dateEnd);
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,4)))
                .andExpect(jsonPath("$.depo").value(depo));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,4)))
                .andExpect(jsonPath("$.number").value(route));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,4)))
                .andExpect(jsonPath("$.depo").value(depo));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,4)))
                .andExpect(jsonPath("$.number").value(route));
    }

    // Если указано только date_start, depo и route - выдать общее количество рейсов
    // по указанному депо по указанному маршруту с указанной даты по текущий день.
    @Test
    void getScheduleTest13() throws Exception {
        String route = "3";
        long depo = 1;
        LocalDate dateStart = LocalDate.of(2023,11,5);
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setRoute(route);
        filterRequest.setDate_start(dateStart);
        filterRequest.setDepo(depo);
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,8)))
                .andExpect(jsonPath("$.depo").value(depo))
                .andExpect(jsonPath("$.number").value(route));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,4)));
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
        mockMvc.perform(get(GET_SCHEDULE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value(LocalDate.of(2023,11,4)));
    }
}
