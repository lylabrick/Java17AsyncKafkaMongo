// ─────────────────────────────────────────────
// TipoCuenta.java
// ─────────────────────────────────────────────
package fintech.cuentas.domain.model;

import java.math.BigDecimal;

/**
 * Jerarquía sellada: cada tipo de cuenta lleva sus propios parámetros de negocio.
 * Al ser sealed, el switch del validador puede ser exhaustivo sin default.
 */
public sealed interface TipoCuenta
        permits TipoCuenta.CajaAhorro, TipoCuenta.CuentaCorriente, TipoCuenta.CuentaSueldo {

    record CajaAhorro(BigDecimal saldoMinimo, boolean monedaExtranjera) implements TipoCuenta {
        public CajaAhorro {
            if (saldoMinimo == null || saldoMinimo.signum() < 0) {
                throw new IllegalArgumentException("saldoMinimo debe ser >= 0");
            }
        }
        public CajaAhorro() { this(BigDecimal.ZERO, false); }
    }

    record CuentaCorriente(BigDecimal limiteDescubierto, String cuit) implements TipoCuenta {
        public CuentaCorriente {
            if (limiteDescubierto == null || limiteDescubierto.signum() < 0) {
                throw new IllegalArgumentException("limiteDescubierto debe ser >= 0");
            }
            if (cuit == null || !cuit.matches("\\d{2}-?\\d{8}-?\\d")) {
                throw new IllegalArgumentException("CUIT inválido: " + cuit);
            }
        }
    }

    record CuentaSueldo(String cuitEmpleador, String convenio) implements TipoCuenta {
        public CuentaSueldo {
            if (cuitEmpleador == null || !cuitEmpleador.matches("\\d{2}-?\\d{8}-?\\d")) {
                throw new IllegalArgumentException("CUIT del empleador inválido: " + cuitEmpleador);
            }
            convenio = (convenio == null || convenio.isBlank()) ? "SIN_CONVENIO" : convenio.trim();
        }
    }
}