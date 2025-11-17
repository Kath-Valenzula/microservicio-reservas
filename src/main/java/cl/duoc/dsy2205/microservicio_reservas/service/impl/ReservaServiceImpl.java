package cl.duoc.dsy2205.microservicio_reservas.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.repository.ReservaRepository;
import cl.duoc.dsy2205.microservicio_reservas.service.ReservaService;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository repository;

    public ReservaServiceImpl(ReservaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reserva> findAll() { return repository.findAll(); }

    @Override
    public Optional<Reserva> findById(Long idReserva) {
        Objects.requireNonNull(idReserva, "idReserva debe no ser null");
        Reserva r = repository.findById(idReserva)
                .orElseThrow(() -> new cl.duoc.dsy2205.microservicio_reservas.exception.ResourceNotFoundException("Reserva no encontrada id=" + idReserva));
        return Optional.of(r);
    }

    @Override
    public Reserva create(Reserva r) {
        Objects.requireNonNull(r, "reserva debe no ser null");
        r.setIdReserva(null);
        return Objects.requireNonNull(repository.save(r));
    }

    @Override
    public Optional<Reserva> update(Long idReserva, Reserva r) {
        Objects.requireNonNull(idReserva, "idReserva debe no ser null");
        Objects.requireNonNull(r, "reserva debe no ser null");
        Reserva existing = repository.findById(idReserva)
                .orElseThrow(() -> new cl.duoc.dsy2205.microservicio_reservas.exception.ResourceNotFoundException("Reserva no encontrada id=" + idReserva));
        existing.setFecha(r.getFecha());
        existing.setHoraInicio(r.getHoraInicio());
        existing.setHoraFin(r.getHoraFin());
        existing.setIdLab(r.getIdLab());
        existing.setIdUsuario(r.getIdUsuario());
        return Optional.of(Objects.requireNonNull(repository.save(existing)));
    }

    @Override
    public boolean delete(Long idReserva) {
        Objects.requireNonNull(idReserva, "idReserva debe no ser null");
        return repository.findById(idReserva).map(x -> {
            try {
                repository.delete(Objects.requireNonNull(x));
                return true;
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                throw new cl.duoc.dsy2205.microservicio_reservas.exception.IntegrityViolationException("No se puede eliminar la reserva por restricciones de integridad");
            }
        }).orElse(false);
    }

    @Override
    public List<Reserva> porUsuario(Long idUsuario) { Objects.requireNonNull(idUsuario, "idUsuario debe no ser null"); return repository.findByIdUsuario(idUsuario); }

    @Override
    public List<Reserva> porLaboratorio(Long idLab) { Objects.requireNonNull(idLab, "idLab debe no ser null"); return repository.findByIdLab(idLab); }

    @Override
    public List<Reserva> porFecha(LocalDate fecha) { Objects.requireNonNull(fecha, "fecha debe no ser null"); return repository.findByFecha(fecha); }

    @Override
    public List<Reserva> porRangoFechas(LocalDate desde, LocalDate hasta) { Objects.requireNonNull(desde, "desde debe no ser null"); Objects.requireNonNull(hasta, "hasta debe no ser null"); return repository.findByFechaBetween(desde, hasta); }
}
