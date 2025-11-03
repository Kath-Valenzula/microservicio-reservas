package cl.duoc.dsy2205.microservicio_reservas.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "RESERVAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reserva implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVA")
    private Long idReserva;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "HORA_INICIO", nullable = false, length = 10)
    private String horaInicio;

    @Column(name = "HORA_FIN", nullable = false, length = 10)
    private String horaFin;

    @Column(name = "ID_LAB", nullable = false)
    private Long idLab;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long idUsuario;
}
