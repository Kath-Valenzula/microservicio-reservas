package cl.duoc.dsy2205.microservicio_reservas.mapper;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cl.duoc.dsy2205.microservicio_reservas.dto.ReservaDTO;
import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;

class ReservaMapperTest {

    @Test
    void toEntity_mapsFields() {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(1L);
        dto.setFecha(LocalDate.of(2025, 1, 1));
        dto.setHoraInicio("09:00");
        dto.setHoraFin("10:00");
        dto.setIdLab(2L);
        dto.setIdUsuario(3L);

        Reserva entity = ReservaMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(1L, entity.getIdReserva());
        assertEquals("09:00", entity.getHoraInicio());
    }

    @Test
    void toDto_mapsFields() {
        Reserva r = new Reserva();
        r.setIdReserva(4L);
        r.setHoraInicio("11:00");
        r.setHoraFin("12:00");

        ReservaDTO dto = ReservaMapper.toDto(r);

        assertNotNull(dto);
        assertEquals(4L, dto.getIdReserva());
        assertEquals("11:00", dto.getHoraInicio());
    }
}
