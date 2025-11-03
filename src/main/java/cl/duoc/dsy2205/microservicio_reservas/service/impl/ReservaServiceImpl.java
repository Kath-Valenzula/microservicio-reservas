package cl.duoc.dsy2205.microservicio_reservas.service.impl;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.repository.ReservaRepository;
import cl.duoc.dsy2205.microservicio_reservas.service.ReservaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository repository;

    public ReservaServiceImpl(ReservaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reserva> findAll() { return repository.findAll(); }

    @Override
    public Optional<Reserva> findById(Long idReserva) { return repository.findById(idReserva); }

    @Override
    public Reserva create(Reserva r) {
        r.setIdReserva(null);
        return repository.save(r);
    }

    @Override
    public Optional<Reserva> update(Long idReserva, Reserva r) {
        return repository.findById(idReserva).map(ex -> {
            ex.setFecha(r.getFecha());
            ex.setHoraInicio(r.getHoraInicio());
            ex.setHoraFin(r.getHoraFin());
            ex.setIdLab(r.getIdLab());
            ex.setIdUsuario(r.getIdUsuario());
            return repository.save(ex);
        });
    }

    @Override
    public boolean delete(Long idReserva) {
        return repository.findById(idReserva).map(x -> { repository.delete(x); return true;}).orElse(false);
    }

    @Override
    public List<Reserva> porUsuario(Long idUsuario) { return repository.findByIdUsuario(idUsuario); }

    @Override
    public List<Reserva> porLaboratorio(Long idLab) { return repository.findByIdLab(idLab); }

    @Override
    public List<Reserva> porFecha(LocalDate fecha) { return repository.findByFecha(fecha); }

    @Override
    public List<Reserva> porRangoFechas(LocalDate desde, LocalDate hasta) { return repository.findByFechaBetween(desde, hasta); }
}
