package cl.duoc.dsy2205.microservicio_reservas.service;

import java.util.List;
import java.util.Optional;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;

public interface ReservaService {
    List<Reserva> findAll();
    Optional<Reserva> findById(Long id);
    Reserva save(Reserva reserva);
    void deleteById(Long id);
}
