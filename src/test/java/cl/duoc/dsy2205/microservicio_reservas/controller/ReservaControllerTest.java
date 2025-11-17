package cl.duoc.dsy2205.microservicio_reservas.controller;

import java.time.LocalDate;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.service.ReservaService;

@WebMvcTest(ReservaController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal", "nullness"})
public class ReservaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean ReservaService service;

    @Test
    void createHappyPath() throws Exception {
        Reserva input = new Reserva();
        input.setFecha(LocalDate.now());
        input.setHoraInicio("09:00");
        input.setHoraFin("10:00");
        input.setIdLab(1L);
        input.setIdUsuario(1L);
        Reserva created = new Reserva();
        created.setIdReserva(1L);
        created.setFecha(input.getFecha());
        created.setHoraInicio(input.getHoraInicio());
        created.setHoraFin(input.getHoraFin());
        created.setIdLab(input.getIdLab());
        created.setIdUsuario(input.getIdUsuario());
        when(service.create(any(Reserva.class))).thenReturn(created);

    mvc.perform(post("/api/reservas")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(mapper.writeValueAsString(input))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/reservas/1"));
    }

    @Test
    void createNullIdFromService_shouldReturnServerError() throws Exception {
        Reserva input = new Reserva();
        input.setFecha(LocalDate.now());
        input.setHoraInicio("11:00");
        input.setHoraFin("12:00");
        input.setIdLab(2L);
        input.setIdUsuario(2L);
        Reserva created = new Reserva();
        created.setFecha(input.getFecha());
        created.setHoraInicio(input.getHoraInicio());
        created.setHoraFin(input.getHoraFin());
        created.setIdLab(input.getIdLab());
        created.setIdUsuario(input.getIdUsuario());
        // created.idReserva intentionally left null
        when(service.create(any(Reserva.class))).thenReturn(created);

    mvc.perform(post("/api/reservas")
            .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
            .content(Objects.requireNonNull(mapper.writeValueAsString(input))))
                .andExpect(status().is5xxServerError());
    }
}
