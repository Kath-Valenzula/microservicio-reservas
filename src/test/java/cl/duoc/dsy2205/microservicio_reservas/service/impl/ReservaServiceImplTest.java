package cl.duoc.dsy2205.microservicio_reservas.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import cl.duoc.dsy2205.microservicio_reservas.entity.Laboratorio;
import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.exception.IntegrityViolationException;
import cl.duoc.dsy2205.microservicio_reservas.exception.ResourceNotFoundException;
import cl.duoc.dsy2205.microservicio_reservas.repository.LaboratorioRepository;
import cl.duoc.dsy2205.microservicio_reservas.repository.ReservaRepository;
import cl.duoc.dsy2205.microservicio_reservas.security.AuthUsuario;
import cl.duoc.dsy2205.microservicio_reservas.security.AuthUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock ReservaRepository repository;
    @Mock AuthUsuarioRepository authUsuarioRepository;
    @Mock LaboratorioRepository laboratorioRepository;

    private ReservaServiceImpl service;

    @BeforeEach
    void setup() {
        service = new ReservaServiceImpl(repository, authUsuarioRepository, laboratorioRepository);
    }

    @Test
    void finders_delegateToRepo() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        when(repository.findAll()).thenReturn(List.of());
        when(repository.findByIdUsuario(1L)).thenReturn(List.of());
        when(repository.findByIdLab(2L)).thenReturn(List.of());
        when(repository.findByFecha(date)).thenReturn(List.of());
        when(repository.findByFechaBetween(date, date.plusDays(1))).thenReturn(List.of());

        assertEquals(0, service.findAll().size());
        assertEquals(0, service.porUsuario(1L).size());
        assertEquals(0, service.porLaboratorio(2L).size());
        assertEquals(0, service.porFecha(date).size());
        assertEquals(0, service.porRangoFechas(date, date.plusDays(1)).size());
    }

    @Test
    void create_happyPath() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Reserva input = baseReserva(date, "09:00", "10:00", 1L, 2L);
        input.setIdReserva(99L);
        stubUsuarioExists(2L);
        stubLaboratorioExists(1L, 2);
        when(repository.findByIdUsuarioAndFecha(2L, date)).thenReturn(List.of());
        when(repository.findByIdLabAndFecha(1L, date)).thenReturn(List.of());
        when(repository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = service.create(input);

        assertNull(saved.getIdReserva());
        verify(repository).save(any(Reserva.class));
    }

    @Test
    void create_invalidTimeThrows() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Reserva input = baseReserva(date, "25:00", "10:00", 1L, 2L);
        stubUsuarioExists(2L);
        stubLaboratorioExists(1L, 1);

        assertThrows(IntegrityViolationException.class, () -> service.create(input));
    }

    @Test
    void create_userConflictThrows() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Reserva input = baseReserva(date, "09:00", "10:00", 1L, 2L);
        Reserva existing = baseReserva(date, "09:30", "10:30", 1L, 2L);
        stubUsuarioExists(2L);
        stubLaboratorioExists(1L, 2);
        when(repository.findByIdUsuarioAndFecha(2L, date)).thenReturn(List.of(existing));

        assertThrows(IntegrityViolationException.class, () -> service.create(input));
    }

    @Test
    void create_labCapacityConflictThrows() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Reserva input = baseReserva(date, "09:00", "10:00", 1L, 2L);
        Reserva existing = baseReserva(date, "09:30", "10:30", 1L, 3L);
        stubUsuarioExists(2L);
        stubLaboratorioExists(1L, 1);
        when(repository.findByIdUsuarioAndFecha(2L, date)).thenReturn(List.of());
        when(repository.findByIdLabAndFecha(1L, date)).thenReturn(List.of(existing));

        assertThrows(IntegrityViolationException.class, () -> service.create(input));
    }

    @Test
    void update_happyPath() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Reserva existing = baseReserva(date, "09:00", "10:00", 1L, 2L);
        existing.setIdReserva(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(existing));
        stubUsuarioExists(2L);
        stubLaboratorioExists(1L, 2);
        when(repository.findByIdUsuarioAndFecha(2L, date)).thenReturn(List.of());
        when(repository.findByIdLabAndFecha(1L, date)).thenReturn(List.of());
        when(repository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva update = baseReserva(date, "10:00", "11:00", 1L, 2L);
        Reserva saved = service.update(7L, update).orElseThrow();

        assertEquals("10:00", saved.getHoraInicio());
        assertEquals("11:00", saved.getHoraFin());
    }

    @Test
    void update_missingThrows() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        Reserva update = new Reserva();
        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, update));
    }

    @Test
    void asignar_selectsAvailableLab() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        Laboratorio lab1 = lab(1L, 1);
        Laboratorio lab2 = lab(2L, 2);
        stubUsuarioExists(2L);
        when(laboratorioRepository.findAll()).thenReturn(List.of(lab1, lab2));
        when(laboratorioRepository.findById(2L)).thenReturn(Optional.of(lab2));
        when(repository.findByIdUsuarioAndFecha(2L, date)).thenReturn(List.of());
        when(repository.findByIdLabAndFecha(eq(1L), eq(date))).thenReturn(List.of(baseReserva(date, "09:00", "10:00", 1L, 9L)));
        when(repository.findByIdLabAndFecha(eq(2L), eq(date))).thenReturn(List.of());
        when(repository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva saved = service.asignar(2L, date, "09:00", "10:00");

        assertEquals(2L, saved.getIdLab());
    }

    @Test
    void delete_notFoundReturnsFalse() {
        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertFalse(service.delete(5L));
    }

    @Test
    void delete_integrityViolationThrows() {
        Reserva existing = baseReserva(LocalDate.of(2025, 1, 1), "09:00", "10:00", 1L, 2L);
        existing.setIdReserva(3L);
        when(repository.findById(3L)).thenReturn(Optional.of(existing));
        doThrow(new DataIntegrityViolationException("fk")).when(repository).delete(existing);

        assertThrows(IntegrityViolationException.class, () -> service.delete(3L));
    }

    private void stubUsuarioExists(Long idUsuario) {
        AuthUsuario auth = new AuthUsuario();
        auth.setIdUsuario(idUsuario);
        when(authUsuarioRepository.findById(idUsuario)).thenReturn(Optional.of(auth));
    }

    private void stubLaboratorioExists(Long idLab, Integer capacidad) {
        Laboratorio lab = lab(idLab, capacidad);
        when(laboratorioRepository.findById(idLab)).thenReturn(Optional.of(lab));
    }

    private Laboratorio lab(Long idLab, Integer capacidad) {
        Laboratorio lab = new Laboratorio();
        lab.setIdLab(idLab);
        lab.setCapacidad(capacidad);
        return lab;
    }

    private Reserva baseReserva(LocalDate fecha, String inicio, String fin, Long idLab, Long idUsuario) {
        Reserva r = new Reserva();
        r.setFecha(fecha);
        r.setHoraInicio(inicio);
        r.setHoraFin(fin);
        r.setIdLab(idLab);
        r.setIdUsuario(idUsuario);
        return r;
    }
}
