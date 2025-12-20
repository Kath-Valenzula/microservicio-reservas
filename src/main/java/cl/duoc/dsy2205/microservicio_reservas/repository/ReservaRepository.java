package cl.duoc.dsy2205.microservicio_reservas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByIdUsuario(Long idUsuario);
    List<Reserva> findByIdUsuarioAndFecha(Long idUsuario, LocalDate fecha);
    List<Reserva> findByIdLab(Long idLab);
    List<Reserva> findByFecha(LocalDate fecha);
    List<Reserva> findByFechaBetween(LocalDate desde, LocalDate hasta);
    List<Reserva> findByIdLabAndFecha(Long idLab, LocalDate fecha);
}
