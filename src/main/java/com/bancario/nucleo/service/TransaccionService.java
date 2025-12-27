package com.bancario.nucleo.service;

import com.bancario.nucleo.dto.TransaccionRequestDTO;
import com.bancario.nucleo.dto.TransaccionResponseDTO;
import com.bancario.nucleo.exception.BusinessException;
import com.bancario.nucleo.model.Transaccion;
import com.bancario.nucleo.repository.TransaccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    @Transactional
    public TransaccionResponseDTO procesarTransaccion(@NonNull TransaccionRequestDTO request) {
        UUID idInstruccion = request.getIdInstruccion();
        log.info("Iniciando procesamiento de transacción: {}", idInstruccion);

        // 1. Validar duplicados (Idempotencia)
        if (idInstruccion != null && transaccionRepository.existsById(idInstruccion)) {
            log.warn("Transacción duplicada detectada: {}", idInstruccion);
            throw new BusinessException("La transacción con ID " + idInstruccion + " ya ha sido procesada.");
        }

        // 2. Mapear DTO a Entidad
        Transaccion transaccion = new Transaccion();
        transaccion.setIdInstruccion(request.getIdInstruccion());
        transaccion.setIdMensaje(request.getIdMensaje());
        transaccion.setReferenciaRed(request.getReferenciaRed());
        transaccion.setMonto(request.getMonto());
        transaccion.setMoneda(request.getMoneda());
        transaccion.setCodigoBicOrigen(request.getCodigoBicOrigen());
        transaccion.setCodigoBicDestino(request.getCodigoBicDestino());
        transaccion.setEstado("RECEIVED");
        transaccion.setFechaCreacion(LocalDateTime.now());

        // 3. Persistir estado inicial
        Transaccion guardada = transaccionRepository.save(transaccion);
        log.info("Transacción registrada con éxito. Estado: RECEIVED");

        // 4. Lógica de Orquestación (Simulada para los otros 4 servicios)
        try {
            // Lógica interna: Aquí llamarías a los otros servicios usando el UUID como
            // vínculo
            log.debug("Notificando a microservicios: Directorio -> Contabilidad -> Conector");

            // Simulamos ruteo exitoso
            guardada.setEstado("COMPLETED");
            transaccionRepository.save(guardada);
            log.info("Orquestación finalizada: COMPLETED");

        } catch (Exception e) {
            log.error("Error en orquestación: {}", e.getMessage());
            guardada.setEstado("FAILED");
            transaccionRepository.save(guardada);
            throw e;
        }

        return mapToResponse(guardada);
    }

    public TransaccionResponseDTO obtenerTransaccion(@NonNull UUID id) {
        Transaccion transaccion = transaccionRepository.findById(id)
                .orElseThrow(
                        () -> new com.bancario.nucleo.exception.ResourceNotFoundException("Transacción no encontrada"));
        return mapToResponse(transaccion);
    }

    private TransaccionResponseDTO mapToResponse(Transaccion entity) {
        return TransaccionResponseDTO.builder()
                .idInstruccion(entity.getIdInstruccion())
                .idMensaje(entity.getIdMensaje())
                .referenciaRed(entity.getReferenciaRed())
                .monto(entity.getMonto())
                .moneda(entity.getMoneda())
                .codigoBicOrigen(entity.getCodigoBicOrigen())
                .codigoBicDestino(entity.getCodigoBicDestino())
                .estado(entity.getEstado())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
