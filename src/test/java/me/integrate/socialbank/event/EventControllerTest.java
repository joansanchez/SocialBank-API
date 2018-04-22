package me.integrate.socialbank.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    @WithMockUser
    void shouldReturnCreatedStatus() throws Exception {
        Event event = EventTestUtils.createEvent();
        given(eventService.saveEvent(any())).willReturn(event);
        this.mockMvc.perform(
                post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(event)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void shouldReturnOkStatus() throws Exception {
        int id = 1;
        given(eventService.getEventById(id)).willReturn(EventTestUtils.createEvent());

        this.mockMvc.perform(
                get("/events/"+id))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldReturnListOfEvents() throws Exception {
        Event e1 = EventTestUtils.createEvent("a@a.a");
        Event e2 = EventTestUtils.createEvent("b@b.b");
        List<Event> le = new ArrayList<>();
        le.add(e1); le.add(e2);

        when(eventService.getEvents()).thenReturn(le);
        this.mockMvc.perform(get("/events/"))
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(le.size())))
                .andExpect(jsonPath("$.[*].creatorEmail", hasItems("a@a.a", "b@b.b")))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundStatus() throws Exception {
        int id = 123;
        given(eventService.getEventById(id))
                .willThrow(EventNotFoundException.class);

        this.mockMvc.perform(
                get("/events/"+id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnUnauthorizedStatus() throws Exception {
        this.mockMvc.perform(
                get("/events/1"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser
    void givenEventPostWithIniDateNotLesThanEndDateShouldReturnBadRequestStatus() throws Exception{
        Date iniDate, endDate;
        iniDate = endDate = new Date();
        try {
            iniDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-03-03");
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-03-03");
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        Event event = EventTestUtils.createEvent(iniDate, endDate);
        this.mockMvc.perform(
                post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(event)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser
    void givenEventPostWithIniDateLessThanCurrentDateShouldReturnBadRequestStatus() throws Exception{
        Date iniDate, endDate;
        iniDate = endDate = new Date();
        try {
            iniDate = new SimpleDateFormat("yyyy-MM-dd").parse("1990-03-03");
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse("2019-03-03");
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        Event event = EventTestUtils.createEvent(iniDate, endDate);
        this.mockMvc.perform(
                post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(event)))
                .andExpect(status().isBadRequest());
    }
}