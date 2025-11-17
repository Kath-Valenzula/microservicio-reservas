package cl.duoc.dsy2205.microservicio_reservas.mapper;

import cl.duoc.dsy2205.microservicio_reservas.dto.ReservaDTO;
import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;

public final class ReservaMapper {
    private ReservaMapper() {}

    public static Reserva toEntity(ReservaDTO dto) {
        if (dto == null) return null;
        Reserva r = new Reserva();
        r.setIdReserva(dto.getIdReserva());
        r.setFecha(dto.getFecha());
        r.setHoraInicio(dto.getHoraInicio());
        r.setHoraFin(dto.getHoraFin());
        r.setIdLab(dto.getIdLab());
        r.setIdUsuario(dto.getIdUsuario());
        return r;
    }

    public static ReservaDTO toDto(Reserva r) {
        if (r == null) return null;
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(r.getIdReserva());
        dto.setFecha(r.getFecha());
        dto.setHoraInicio(r.getHoraInicio());
        dto.setHoraFin(r.getHoraFin());
        dto.setIdLab(r.getIdLab());
        dto.setIdUsuario(r.getIdUsuario());
        return dto;
    }
}
