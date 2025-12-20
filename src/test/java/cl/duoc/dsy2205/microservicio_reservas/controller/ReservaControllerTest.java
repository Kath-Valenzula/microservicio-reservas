package cl.duoc.dsy2205.microservicio_reservas.controller;

import java.time.LocalDate;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.service.ReservaService;

@WebMvcTest(ReservaController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal", "nullness"})
class ReservaControllerTest {

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

    @Test
    void asignarHappyPath() throws Exception {
        Reserva created = new Reserva();
        created.setIdReserva(5L);
        created.setFecha(LocalDate.now());
        created.setHoraInicio("09:00");
        created.setHoraFin("10:00");
        created.setIdLab(1L);
        created.setIdUsuario(2L);
        when(service.asignar(any(Long.class), any(LocalDate.class), any(String.class), any(String.class))).thenReturn(created);

        String body = "{\"fecha\":\"2025-11-06\",\"horaInicio\":\"09:00\",\"horaFin\":\"10:00\",\"idUsuario\":2}";
        mvc.perform(post("/api/reservas/asignar")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/reservas/5"));
    }

    @Test
    void listarReturnsItems() throws Exception {
        Reserva r = new Reserva();
        r.setIdReserva(1L);
        r.setFecha(LocalDate.now());
        r.setHoraInicio("09:00");
        r.setHoraFin("10:00");
        r.setIdLab(1L);
        r.setIdUsuario(1L);
        when(service.findAll()).thenReturn(List.of(r));

        mvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value(1));
    }

    @Test
    void obtenerReturnsItem() throws Exception {
        Reserva r = new Reserva();
        r.setIdReserva(2L);
        r.setFecha(LocalDate.now());
        r.setHoraInicio("09:00");
        r.setHoraFin("10:00");
        r.setIdLab(1L);
        r.setIdUsuario(1L);
        when(service.findById(2L)).thenReturn(java.util.Optional.of(r));

        mvc.perform(get("/api/reservas/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(2));
    }

    @Test
    void actualizarReturnsItem() throws Exception {
        Reserva updated = new Reserva();
        updated.setIdReserva(3L);
        updated.setFecha(LocalDate.now());
        updated.setHoraInicio("11:00");
        updated.setHoraFin("12:00");
        updated.setIdLab(2L);
        updated.setIdUsuario(4L);
        when(service.update(any(Long.class), any(Reserva.class))).thenReturn(java.util.Optional.of(updated));

        Reserva payload = new Reserva();
        payload.setFecha(LocalDate.now());
        payload.setHoraInicio("11:00");
        payload.setHoraFin("12:00");
        payload.setIdLab(2L);
        payload.setIdUsuario(4L);

        mvc.perform(put("/api/reservas/3")
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(Objects.requireNonNull(mapper.writeValueAsString(payload))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(3));
    }

    @Test
    void eliminarReturnsNoContent() throws Exception {
        when(service.delete(9L)).thenReturn(true);
        mvc.perform(delete("/api/reservas/9"))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPorUsuarioReturnsItems() throws Exception {
        Reserva r = new Reserva();
        r.setIdReserva(4L);
        r.setFecha(LocalDate.now());
        r.setHoraInicio("09:00");
        r.setHoraFin("10:00");
        r.setIdLab(1L);
        r.setIdUsuario(7L);
        when(service.porUsuario(7L)).thenReturn(List.of(r));

        mvc.perform(get("/api/reservas/buscar/usuario?id=7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(7));
    }

    @Test
    void buscarPorLabReturnsItems() throws Exception {
        Reserva r = new Reserva();
        r.setIdReserva(5L);
        r.setFecha(LocalDate.now());
        r.setHoraInicio("09:00");
        r.setHoraFin("10:00");
        r.setIdLab(3L);
        r.setIdUsuario(2L);
        when(service.porLaboratorio(3L)).thenReturn(List.of(r));

        mvc.perform(get("/api/reservas/buscar/lab?id=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idLab").value(3));
    }

    @Test
    void buscarPorFechaReturnsItems() throws Exception {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Reserva r = new Reserva();
        r.setIdReserva(6L);
        r.setFecha(date);
        r.setHoraInicio("09:00");
        r.setHoraFin("10:00");
        r.setIdLab(3L);
        r.setIdUsuario(2L);
        when(service.porFecha(date)).thenReturn(List.of(r));

        mvc.perform(get("/api/reservas/buscar/fecha?dia=2025-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value(6));
    }

    @Test
    void buscarPorRangoReturnsItems() throws Exception {
        LocalDate desde = LocalDate.of(2025, 1, 1);
        LocalDate hasta = LocalDate.of(2025, 1, 5);
        Reserva r = new Reserva();
        r.setIdReserva(7L);
        r.setFecha(desde);
        r.setHoraInicio("09:00");
        r.setHoraFin("10:00");
        r.setIdLab(3L);
        r.setIdUsuario(2L);
        when(service.porRangoFechas(desde, hasta)).thenReturn(List.of(r));

        mvc.perform(get("/api/reservas/buscar/rango?desde=2025-01-01&hasta=2025-01-05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value(7));
    }
}
