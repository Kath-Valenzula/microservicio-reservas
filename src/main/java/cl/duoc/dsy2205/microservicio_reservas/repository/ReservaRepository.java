package cl.duoc.dsy2205.microservicio_reservas.repository;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByIdUsuario(Long idUsuario);
    List<Reserva> findByIdLab(Long idLab);
    List<Reserva> findByFecha(LocalDate fecha);
    List<Reserva> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
