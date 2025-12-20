package cl.duoc.dsy2205.microservicio_reservas.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.dsy2205.microservicio_reservas.entity.Laboratorio;
import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.exception.IntegrityViolationException;
import cl.duoc.dsy2205.microservicio_reservas.exception.ResourceNotFoundException;
import cl.duoc.dsy2205.microservicio_reservas.repository.LaboratorioRepository;
import cl.duoc.dsy2205.microservicio_reservas.repository.ReservaRepository;
import cl.duoc.dsy2205.microservicio_reservas.security.AuthUsuarioRepository;
import cl.duoc.dsy2205.microservicio_reservas.service.ReservaService;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository repository;
    private final AuthUsuarioRepository authUsuarioRepository;
    private final LaboratorioRepository laboratorioRepository;

    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?\\d|2[0-3]):[0-5]\\d$");
    private static final String RESERVA_NULL = "reserva debe no ser null";
    private static final String ID_RESERVA_NULL = "idReserva debe no ser null";
    private static final String ID_USUARIO_NULL = "idUsuario debe no ser null";
    private static final String FECHA_NULL = "fecha debe no ser null";
    private static final String HORA_INICIO_NULL = "horaInicio debe no ser null";
    private static final String HORA_FIN_NULL = "horaFin debe no ser null";

    public ReservaServiceImpl(ReservaRepository repository,
                              AuthUsuarioRepository authUsuarioRepository,
                              LaboratorioRepository laboratorioRepository) {
        this.repository = repository;
        this.authUsuarioRepository = authUsuarioRepository;
        this.laboratorioRepository = laboratorioRepository;
    }

    @Override
    public List<Reserva> findAll() { return repository.findAll(); }

    @Override
    public Optional<Reserva> findById(Long idReserva) {
        Objects.requireNonNull(idReserva, ID_RESERVA_NULL);
        Reserva r = repository.findById(idReserva)
                .orElseThrow(() -> new cl.duoc.dsy2205.microservicio_reservas.exception.ResourceNotFoundException("Reserva no encontrada id=" + idReserva));
        return Optional.of(r);
    }

    @Override
    @Transactional
    public Reserva create(Reserva r) {
        Objects.requireNonNull(r, RESERVA_NULL);
        return saveNewReserva(r);
    }

    @Override
    @Transactional
    public Optional<Reserva> update(Long idReserva, Reserva r) {
        Objects.requireNonNull(idReserva, ID_RESERVA_NULL);
        Objects.requireNonNull(r, RESERVA_NULL);
        Reserva existing = repository.findById(idReserva)
                .orElseThrow(() -> new cl.duoc.dsy2205.microservicio_reservas.exception.ResourceNotFoundException("Reserva no encontrada id=" + idReserva));

        // Validar reglas con el contenido solicitado, excluyendo la reserva actual del chequeo de choque
        validateReservaBusinessRules(r, idReserva);

        existing.setFecha(r.getFecha());
        existing.setHoraInicio(r.getHoraInicio());
        existing.setHoraFin(r.getHoraFin());
        existing.setIdLab(r.getIdLab());
        existing.setIdUsuario(r.getIdUsuario());
        try {
            return Optional.of(Objects.requireNonNull(repository.save(existing)));
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new IntegrityViolationException("No se puede actualizar la reserva: el usuario o laboratorio no existe, o hay una restriccion de integridad");
        }
    }

    @Override
    @Transactional
    public boolean delete(Long idReserva) {
        Objects.requireNonNull(idReserva, ID_RESERVA_NULL);
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
    @Transactional
    public Reserva asignar(Long idUsuario, LocalDate fecha, String horaInicio, String horaFin) {
        Objects.requireNonNull(idUsuario, ID_USUARIO_NULL);
        Objects.requireNonNull(fecha, FECHA_NULL);
        Objects.requireNonNull(horaInicio, HORA_INICIO_NULL);
        Objects.requireNonNull(horaFin, HORA_FIN_NULL);
        ensureUsuarioExists(idUsuario);
        LocalTime inicio = parseTimeOrThrow(horaInicio, "horaInicio");
        LocalTime fin = parseTimeOrThrow(horaFin, "horaFin");
        if (!inicio.isBefore(fin)) {
            throw new IntegrityViolationException("Rango horario invalido: horaInicio debe ser menor que horaFin");
        }
        validateUsuarioDisponibilidad(idUsuario, fecha, inicio, fin, null);
        Laboratorio lab = seleccionarLaboratorioDisponible(fecha, inicio, fin);
        Reserva r = new Reserva();
        r.setFecha(fecha);
        r.setHoraInicio(horaInicio);
        r.setHoraFin(horaFin);
        r.setIdUsuario(idUsuario);
        r.setIdLab(lab.getIdLab());
        return saveNewReserva(r);
    }

    @Override
    public List<Reserva> porUsuario(Long idUsuario) { Objects.requireNonNull(idUsuario, ID_USUARIO_NULL); return repository.findByIdUsuario(idUsuario); }

    @Override
    public List<Reserva> porLaboratorio(Long idLab) { Objects.requireNonNull(idLab, "idLab debe no ser null"); return repository.findByIdLab(idLab); }

    @Override
    public List<Reserva> porFecha(LocalDate fecha) { Objects.requireNonNull(fecha, FECHA_NULL); return repository.findByFecha(fecha); }

    @Override
    public List<Reserva> porRangoFechas(LocalDate desde, LocalDate hasta) { Objects.requireNonNull(desde, "desde debe no ser null"); Objects.requireNonNull(hasta, "hasta debe no ser null"); return repository.findByFechaBetween(desde, hasta); }

    private void validateReservaBusinessRules(Reserva r, Long excludeIdReserva) {
        Objects.requireNonNull(r.getFecha(), FECHA_NULL);
        Objects.requireNonNull(r.getIdLab(), "idLab debe no ser null");
        Objects.requireNonNull(r.getIdUsuario(), ID_USUARIO_NULL);
        ensureUsuarioExists(r.getIdUsuario());
        Laboratorio lab = ensureLaboratorioExists(r.getIdLab());
        LocalTime inicio = parseTimeOrThrow(r.getHoraInicio(), "horaInicio");
        LocalTime fin = parseTimeOrThrow(r.getHoraFin(), "horaFin");
        if (!inicio.isBefore(fin)) {
            throw new IntegrityViolationException("Rango horario invalido: horaInicio debe ser menor que horaFin");
        }

        validateUsuarioDisponibilidad(r.getIdUsuario(), r.getFecha(), inicio, fin, excludeIdReserva);
        validateLaboratorioCapacidad(lab, r.getFecha(), inicio, fin, excludeIdReserva);
    }

    private void validateUsuarioDisponibilidad(Long idUsuario, LocalDate fecha, LocalTime inicio, LocalTime fin, Long excludeIdReserva) {
        List<Reserva> existentes = repository.findByIdUsuarioAndFecha(idUsuario, fecha);
        boolean solapa = existentes.stream()
                .filter(x -> excludeIdReserva == null || x.getIdReserva() == null || !excludeIdReserva.equals(x.getIdReserva()))
                .anyMatch(x -> overlaps(inicio, fin, x));
        if (solapa) {
            throw new IntegrityViolationException("Conflicto: el usuario ya tiene una reserva en ese horario");
        }
    }

    private void validateLaboratorioCapacidad(Laboratorio lab, LocalDate fecha, LocalTime inicio, LocalTime fin, Long excludeIdReserva) {
        List<Reserva> existentes = repository.findByIdLabAndFecha(lab.getIdLab(), fecha);
        long solapadas = existentes.stream()
                .filter(x -> excludeIdReserva == null || x.getIdReserva() == null || !excludeIdReserva.equals(x.getIdReserva()))
                .filter(x -> overlaps(inicio, fin, x))
                .count();
        int capacidad = resolveCapacidad(lab);
        if (solapadas >= capacidad) {
            throw new IntegrityViolationException("Conflicto: el laboratorio no tiene cupos disponibles en ese horario");
        }
    }

    private boolean overlaps(LocalTime inicio, LocalTime fin, Reserva existente) {
        LocalTime xInicio = safeParseTime(existente.getHoraInicio());
        LocalTime xFin = safeParseTime(existente.getHoraFin());
        if (xInicio == null || xFin == null) {
            return false;
        }
        return inicio.isBefore(xFin) && fin.isAfter(xInicio);
    }

    private int resolveCapacidad(Laboratorio lab) {
        Integer capacidad = lab.getCapacidad();
        if (capacidad == null || capacidad < 1) {
            return 1;
        }
        return capacidad;
    }

    private Laboratorio seleccionarLaboratorioDisponible(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<Laboratorio> labs = laboratorioRepository.findAll();
        if (labs.isEmpty()) {
            throw new ResourceNotFoundException("No hay laboratorios registrados");
        }
        labs.sort(Comparator.comparing(Laboratorio::getIdLab));
        for (Laboratorio lab : labs) {
            if (labTieneCupo(lab, fecha, inicio, fin)) {
                return lab;
            }
        }
        throw new IntegrityViolationException("Conflicto: no hay laboratorios disponibles en ese horario");
    }

    private boolean labTieneCupo(Laboratorio lab, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<Reserva> existentes = repository.findByIdLabAndFecha(lab.getIdLab(), fecha);
        long solapadas = existentes.stream()
                .filter(x -> overlaps(inicio, fin, x))
                .count();
        return solapadas < resolveCapacidad(lab);
    }

    private LocalTime parseTimeOrThrow(String value, String field) {
        if (value == null || value.isBlank() || !TIME_PATTERN.matcher(value.trim()).matches()) {
            throw new IntegrityViolationException("Formato invalido para " + field + ": use HH:mm");
        }
        return LocalTime.parse(normalizeTime(value.trim()));
    }

    private LocalTime safeParseTime(String value) {
        try {
            if (value == null || value.isBlank() || !TIME_PATTERN.matcher(value.trim()).matches()) {
                return null;
            }
            return LocalTime.parse(normalizeTime(value.trim()));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String normalizeTime(String value) {
        // LocalTime.parse espera HH:mm; si viene H:mm, pad a 0.
        if (value.length() == 4) {
            return "0" + value;
        }
        return value;
    }

    private Reserva saveNewReserva(Reserva r) {
        r.setIdReserva(null);
        validateReservaBusinessRules(r, null);
        try {
            return Objects.requireNonNull(repository.save(r));
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new IntegrityViolationException("No se puede crear la reserva: el usuario o laboratorio no existe, o hay una restriccion de integridad");
        }
    }

    private void ensureUsuarioExists(Long idUsuario) {
        if (authUsuarioRepository.findById(idUsuario).isEmpty()) {
            throw new ResourceNotFoundException("Usuario no encontrado id=" + idUsuario);
        }
    }

    private Laboratorio ensureLaboratorioExists(Long idLab) {
        return laboratorioRepository.findById(idLab)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratorio no encontrado id=" + idLab));
    }
}
