package logistica.envios;

// ─────────────────────────────────────────────
// Envio.java
// ─────────────────────────────────────────────

import java.time.Duration;
import java.time.LocalDateTime;

public sealed interface Envio
        permits Envio.EnPreparacion, Envio.EnTransito, Envio.Entregado, Envio.Devuelto {

    String codigoSeguimiento();

    record EnPreparacion(
            String codigoSeguimiento,
            LocalDateTime fechaCreacion,
            String depositoOrigen
    ) implements Envio {
        public EnPreparacion {
            if (fechaCreacion == null) {
                throw new EstadoEnvioException("fechaCreacion no puede ser null");
            }
        }
    }

    record EnTransito(
            String codigoSeguimiento,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaDespacho,
            String transportista,
            String ciudadActual
    ) implements Envio {
        public EnTransito {
            if (fechaDespacho.isBefore(fechaCreacion)) {
                throw new EstadoEnvioException("fechaDespacho no puede ser anterior a fechaCreacion");
            }
        }
    }

    record Entregado(
            String codigoSeguimiento,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaDespacho,
            LocalDateTime fechaEntrega,
            String receptor,
            LocalDateTime fechaEstimada
    ) implements Envio {
        public Entregado {
            if (fechaEntrega.isBefore(fechaDespacho)) {
                throw new EstadoEnvioException("fechaEntrega no puede ser anterior a fechaDespacho");
            }
        }
    }

    record Devuelto(
            String codigoSeguimiento,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaDespacho,
            LocalDateTime fechaDevolucion,
            String motivo,
            LocalDateTime fechaEstimada
    ) implements Envio {
        public Devuelto {
            if (motivo == null || motivo.isBlank()) {
                throw new EstadoEnvioException("motivo de devolución requerido");
            }
        }
    }
}