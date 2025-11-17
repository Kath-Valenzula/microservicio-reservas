package cl.duoc.dsy2205.microservicio_reservas.controller;

import cl.duoc.dsy2205.microservicio_reservas.entity.Reserva;
import cl.duoc.dsy2205.microservicio_reservas.service.ReservaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Reserva> listar() { log.info("GET /api/reservas"); return service.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtener(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reserva> crear(@Valid @RequestBody Reserva r) {
        log.info("POST /api/reservas - creating reserva: user={} lab={}", r.getIdUsuario(), r.getIdLab());
        Reserva creada = service.create(r);
        return ResponseEntity.created(URI.create("/api/reservas/" + creada.getIdReserva())).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> actualizar(@PathVariable Long id, @Valid @RequestBody Reserva r) {
        return service.update(id, r)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/reservas/{}", id);
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Búsquedas
    @GetMapping("/buscar/usuario")
    public List<Reserva> porUsuario(@RequestParam("id") Long idUsuario) {
        return service.porUsuario(idUsuario);
    }

    @GetMapping("/buscar/lab")
    public List<Reserva> porLab(@RequestParam("id") Long idLab) {
        return service.porLaboratorio(idLab);
    }

    @GetMapping("/buscar/fecha")
    public List<Reserva> porFecha(@RequestParam("dia")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia) {
        return service.porFecha(dia);
    }

    @GetMapping("/buscar/rango")
    public List<Reserva> porRango(@RequestParam("desde")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                  @RequestParam("hasta")
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.porRangoFechas(desde, hasta);
    }
}
