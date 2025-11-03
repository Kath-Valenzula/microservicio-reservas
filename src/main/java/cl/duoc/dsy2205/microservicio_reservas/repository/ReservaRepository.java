package cl.duoc.dsy2205.microservicio_reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
