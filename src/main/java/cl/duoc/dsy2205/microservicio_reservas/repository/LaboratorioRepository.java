package cl.duoc.dsy2205.microservicio_reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.dsy2205.microservicio_reservas.entity.Laboratorio;

public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {
}
