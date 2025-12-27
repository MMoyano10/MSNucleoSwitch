package com.bancario.nucleo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "respaldo_idempotencia")
public class RespaldoIdempotencia {

    @Id
    @Column(name = "id_instruccion")
    private UUID idInstruccion;

    @Column(name = "hash_contenido", nullable = false)
    private String hashContenido;

    @Column(name = "cuerpo_respuesta", columnDefinition = "jsonb")
    private String cuerpoRespuesta;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_instruccion")
    private Transaccion transaccion;

    public RespaldoIdempotencia() {
    }

    public RespaldoIdempotencia(UUID idInstruccion) {
        this.idInstruccion = idInstruccion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RespaldoIdempotencia that = (RespaldoIdempotencia) o;
        return Objects.equals(idInstruccion, that.idInstruccion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idInstruccion);
    }

    @Override
    public String toString() {
        return "RespaldoIdempotencia{" +
                "idInstruccion=" + idInstruccion +
                ", hashContenido='" + hashContenido + '\'' +
                ", cuerpoRespuesta='" + cuerpoRespuesta + '\'' +
                ", fechaExpiracion=" + fechaExpiracion +
                '}';
    }
}
