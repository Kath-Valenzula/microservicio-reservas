package cl.duoc.dsy2205.microservicio_reservas.service;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    List<Reserva> findAll();
    Optional<Reserva> findById(Long idReserva);
    Reserva create(Reserva r);
    Optional<Reserva> update(Long idReserva, Reserva r);
    boolean delete(Long idReserva);
    Reserva asignar(Long idUsuario, LocalDate fecha, String horaInicio, String horaFin);

    List<Reserva> porUsuario(Long idUsuario);
    List<Reserva> porLaboratorio(Long idLab);
    List<Reserva> porFecha(LocalDate fecha);
    List<Reserva> porRangoFechas(LocalDate desde, LocalDate hasta);
}
