package cl.duoc.dsy2205.microservicio_reservas.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUsuarioRepository extends JpaRepository<AuthUsuario, Long> {
    Optional<AuthUsuario> findByCorreoIgnoreCase(String correo);
}
