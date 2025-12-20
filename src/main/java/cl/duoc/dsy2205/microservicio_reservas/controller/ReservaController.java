package cl.duoc.dsy2205.microservicio_reservas.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.dsy2205.microservicio_reservas.dto.ReservaDTO;
import cl.duoc.dsy2205.microservicio_reservas.dto.ReservaAsignacionDTO;
import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.mapper.ReservaMapper;
import cl.duoc.dsy2205.microservicio_reservas.service.ReservaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReservaDTO> listar() { log.info("GET /api/reservas"); return service.findAll().stream().map(ReservaMapper::toDto).toList(); }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtener(@PathVariable Long id) {
        Reserva r = service.findById(id).orElse(null);
        return ResponseEntity.ok(ReservaMapper.toDto(r));
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody ReservaDTO rDto) {
        Reserva r = ReservaMapper.toEntity(rDto);
        log.info("POST /api/reservas - creating reserva: user={} lab={}", r.getIdUsuario(), r.getIdLab());
        Reserva creada = service.create(r);
        Long id = Objects.requireNonNull(creada.getIdReserva(), "Created reserva id is null");
        URI location = Objects.requireNonNull(URI.create("/api/reservas/" + id));
        return ResponseEntity.created(location).body(ReservaMapper.toDto(creada));
    }

    @PostMapping("/asignar")
    public ResponseEntity<ReservaDTO> asignar(@Valid @RequestBody ReservaAsignacionDTO dto) {
        log.info("POST /api/reservas/asignar - user={} fecha={}", dto.getIdUsuario(), dto.getFecha());
        Reserva creada = service.asignar(dto.getIdUsuario(), dto.getFecha(), dto.getHoraInicio(), dto.getHoraFin());
        Long id = Objects.requireNonNull(creada.getIdReserva(), "Created reserva id is null");
        URI location = Objects.requireNonNull(URI.create("/api/reservas/" + id));
        return ResponseEntity.created(location).body(ReservaMapper.toDto(creada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReservaDTO rDto) {
        Reserva r = ReservaMapper.toEntity(rDto);
        return service.update(id, r)
                .map(res -> ResponseEntity.ok(ReservaMapper.toDto(res)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/reservas/{}", id);
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Búsquedas
    @GetMapping("/buscar/usuario")
    public List<ReservaDTO> porUsuario(@RequestParam("id") Long idUsuario) {
        return service.porUsuario(idUsuario).stream().map(ReservaMapper::toDto).toList();
    }

    @GetMapping("/buscar/lab")
    public List<ReservaDTO> porLab(@RequestParam("id") Long idLab) {
        return service.porLaboratorio(idLab).stream().map(ReservaMapper::toDto).toList();
    }

    @GetMapping("/buscar/fecha")
    public List<ReservaDTO> porFecha(@RequestParam("dia")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia) {
        return service.porFecha(dia).stream().map(ReservaMapper::toDto).toList();
    }

    @GetMapping("/buscar/rango")
    public List<ReservaDTO> porRango(@RequestParam("desde")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                  @RequestParam("hasta")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.porRangoFechas(desde, hasta).stream().map(ReservaMapper::toDto).toList();
    }
}
