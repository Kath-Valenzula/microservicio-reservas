package cl.duoc.dsy2205.microservicio_reservas.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ReservaAsignacionDTO {

    @NotNull
    private LocalDate fecha;

    @NotNull
    @Size(min = 4, max = 5)
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "horaInicio debe tener formato HH:mm")
    private String horaInicio;

    @NotNull
    @Size(min = 4, max = 5)
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "horaFin debe tener formato HH:mm")
    private String horaFin;

    @NotNull
    private Long idUsuario;

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}
