package fintech.cuentas.domain.validation;

// ─────────────────────────────────────────────
// ResultadoValidacion.java
// ─────────────────────────────────────────────

import fintech.cuentas.domain.model.CuentaBancaria;

import java.util.List;

public sealed interface ResultadoValidacion {

    record Valida(CuentaBancaria cuenta) implements ResultadoValidacion {}

    record Invalida(CuentaBancaria cuenta, List<String> motivos) implements ResultadoValidacion {
        public Invalida {
            motivos = List.copyOf(motivos);
        }
    }

    default boolean esValida() { return this instanceof Valida; }
}