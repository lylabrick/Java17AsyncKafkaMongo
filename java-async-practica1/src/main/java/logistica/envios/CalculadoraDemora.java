package logistica.envios;

// ─────────────────────────────────────────────
// CalculadoraDemora.java
// ─────────────────────────────────────────────
import java.time.Duration;

public final class CalculadoraDemora {

    /**
     * Demora respecto de lo estimado. Positivo = tarde, negativo = adelantado,
     * cero cuando el estado todavía no tiene fecha de referencia contra la cual medir.
     */
    public Duration calcularDemora(Envio e) {
        return switch (e) {

            case Envio.EnPreparacion ep ->
                    Duration.ZERO;

            case Envio.EnTransito et ->
                    Duration.between(et.fechaDespacho(), java.time.LocalDateTime.now());

            case Envio.Entregado en when en.fechaEstimada() != null ->
                    Duration.between(en.fechaEstimada(), en.fechaEntrega());

            case Envio.Entregado en ->
                    Duration.ZERO;

            case Envio.Devuelto dv when dv.fechaEstimada() != null ->
                    Duration.between(dv.fechaEstimada(), dv.fechaDevolucion());

            case Envio.Devuelto dv ->
                    Duration.ZERO;
        };
    }

    public String describirDemora(Envio e) {
        Duration demora = calcularDemora(e);
        if (demora.isZero()) return "Sin demora registrada";
        return demora.isNegative()
                ? "Adelantado por " + demora.negated().toHours() + "h"
                : "Demorado por " + demora.toHours() + "h";
    }
}