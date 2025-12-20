package cl.duoc.dsy2205.microservicio_reservas.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import cl.duoc.dsy2205.microservicio_reservas.dto.ReservaAsignacionDTO;
import cl.duoc.dsy2205.microservicio_reservas.dto.ReservaDTO;
import cl.duoc.dsy2205.microservicio_reservas.entity.Laboratorio;
import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.exception.IntegrityViolationException;
import cl.duoc.dsy2205.microservicio_reservas.exception.ResourceNotFoundException;
import cl.duoc.dsy2205.microservicio_reservas.security.AuthUsuario;

class ReservaModelsTest {

    @Test
    void reservaEntity_gettersSetters() {
        Reserva r = new Reserva();
        r.setIdReserva(1L);
        r.setFecha(LocalDate.of(2025, 1, 1));
        r.setHoraInicio("09:00");
        r.setHoraFin("10:00");
        r.setIdLab(2L);
        r.setIdUsuario(3L);

        assertEquals(1L, r.getIdReserva());
        assertEquals("09:00", r.getHoraInicio());
        assertEquals(2L, r.getIdLab());
    }

    @Test
    void laboratorioEntity_gettersSetters() {
        Laboratorio lab = new Laboratorio();
        lab.setIdLab(5L);
        lab.setCapacidad(3);

        assertEquals(5L, lab.getIdLab());
        assertEquals(3, lab.getCapacidad());
    }

    @Test
    void authUsuarioEntity_gettersSetters() {
        AuthUsuario auth = new AuthUsuario();
        auth.setIdUsuario(9L);
        auth.setCorreo("user@inst.cl");
        auth.setPasswordHash("hash");
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        auth.setRoles(roles);

        assertEquals(9L, auth.getIdUsuario());
        assertEquals("user@inst.cl", auth.getCorreo());
        assertEquals("ADMIN", auth.getRoles().iterator().next());
    }

    @Test
    void reservaDtos_gettersSetters() {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(7L);
        dto.setFecha(LocalDate.of(2025, 2, 2));
        dto.setHoraInicio("11:00");
        dto.setHoraFin("12:00");
        dto.setIdLab(1L);
        dto.setIdUsuario(2L);

        ReservaAsignacionDTO asignacion = new ReservaAsignacionDTO();
        asignacion.setFecha(LocalDate.of(2025, 3, 3));
        asignacion.setHoraInicio("13:00");
        asignacion.setHoraFin("14:00");
        asignacion.setIdUsuario(5L);

        assertEquals(7L, dto.getIdReserva());
        assertEquals("11:00", dto.getHoraInicio());
        assertEquals(5L, asignacion.getIdUsuario());
    }

    @Test
    void exceptions_keepMessage() {
        IntegrityViolationException ex1 = new IntegrityViolationException("conflict");
        ResourceNotFoundException ex2 = new ResourceNotFoundException("missing");
        assertEquals("conflict", ex1.getMessage());
        assertEquals("missing", ex2.getMessage());
    }
}
