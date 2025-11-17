package cl.duoc.dsy2205.microservicio_reservas.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReservaDTO {

    private Long idReserva;

    @NotNull
    private LocalDate fecha;

    @NotNull
    @Size(min = 4, max = 5)
    private String horaInicio;

    @NotNull
    @Size(min = 4, max = 5)
    private String horaFin;

    @NotNull
    private Long idLab;

    @NotNull
    private Long idUsuario;

    public Long getIdReserva() { return idReserva; }
    public void setIdReserva(Long idReserva) { this.idReserva = idReserva; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public Long getIdLab() { return idLab; }
    public void setIdLab(Long idLab) { this.idLab = idLab; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
}
