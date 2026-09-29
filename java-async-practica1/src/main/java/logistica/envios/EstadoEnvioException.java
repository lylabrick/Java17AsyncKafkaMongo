package logistica.envios;

// ─────────────────────────────────────────────
// EstadoEnvioException.java
// ─────────────────────────────────────────────

/**
 * Se lanza cuando las fechas o datos de un Envio son inconsistentes
 * con el estado que representan (p. ej. fechaEntrega anterior a fechaDespacho).
 * Se usa como RuntimeException porque la inconsistencia surge de datos
 * ya validados en el constructor compacto de cada record — no es un caso
 * recuperable en tiempo de ejecución normal, sino un error de invariante.
 */
public class EstadoEnvioException extends RuntimeException {

    public EstadoEnvioException(String mensaje) {
        super(mensaje);
    }

    public EstadoEnvioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}