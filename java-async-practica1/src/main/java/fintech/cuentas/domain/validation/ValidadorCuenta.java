package fintech.cuentas.domain.validation;
// ─────────────────────────────────────────────
// ValidadorCuenta.java
// ─────────────────────────────────────────────

import fintech.cuentas.domain.model.CuentaBancaria;
import fintech.cuentas.domain.model.TipoCuenta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de negocio por tipo de cuenta, resueltas con pattern matching for switch.
 * El switch es exhaustivo porque TipoCuenta es sealed: si mañana se agrega
 * un tipo nuevo, este archivo deja de compilar hasta cubrirlo.
 */
public final class ValidadorCuenta {

    private static final BigDecimal TOPE_CAJA_AHORRO_ARS = new BigDecimal("15000000.00");
    private static final BigDecimal TOPE_CAJA_AHORRO_USD = new BigDecimal("100000.00");
    private static final BigDecimal SALDO_MINIMO_SUELDO  = new BigDecimal("0.00");

    public ResultadoValidacion validar(CuentaBancaria cuenta) {
        List<String> motivos = new ArrayList<>();

        switch (cuenta.tipo()) {

            // Caja de ahorro en moneda extranjera: tope propio y sin saldo negativo.
            case TipoCuenta.CajaAhorro ca when ca.monedaExtranjera() -> {
                if (cuenta.saldo().signum() < 0) {
                    motivos.add("Una caja de ahorro no admite saldo negativo");
                }
                if (cuenta.saldo().compareTo(TOPE_CAJA_AHORRO_USD) > 0) {
                    motivos.add("El saldo supera el tope de USD " + TOPE_CAJA_AHORRO_USD
                            + " para cajas de ahorro en moneda extranjera");
                }
                if (cuenta.saldo().compareTo(ca.saldoMinimo()) < 0) {
                    motivos.add("El saldo no alcanza el mínimo de apertura: " + ca.saldoMinimo());
                }
            }

            case TipoCuenta.CajaAhorro ca -> {
                if (cuenta.saldo().signum() < 0) {
                    motivos.add("Una caja de ahorro no admite saldo negativo");
                }
                if (cuenta.saldo().compareTo(TOPE_CAJA_AHORRO_ARS) > 0) {
                    motivos.add("El saldo supera el tope de ARS " + TOPE_CAJA_AHORRO_ARS
                            + "; corresponde migrar a cuenta corriente");
                }
                if (cuenta.saldo().compareTo(ca.saldoMinimo()) < 0) {
                    motivos.add("El saldo no alcanza el mínimo de apertura: " + ca.saldoMinimo());
                }
            }

            // Cuenta corriente: admite descubierto, pero acotado al límite pactado.
            case TipoCuenta.CuentaCorriente cc when cuenta.saldo().signum() < 0 -> {
                BigDecimal descubiertoUsado = cuenta.saldo().abs();
                if (descubiertoUsado.compareTo(cc.limiteDescubierto()) > 0) {
                    motivos.add("El descubierto de " + descubiertoUsado
                            + " excede el límite pactado de " + cc.limiteDescubierto());
                }
                if (cc.limiteDescubierto().signum() == 0) {
                    motivos.add("La cuenta no tiene acuerdo de descubierto habilitado");
                }
            }

            case TipoCuenta.CuentaCorriente cc -> {
                if (cc.limiteDescubierto().compareTo(new BigDecimal("50000000.00")) > 0) {
                    motivos.add("El límite de descubierto requiere aprobación del comité de riesgo");
                }
            }

            // Cuenta sueldo: nunca negativa y con empleador distinto del titular.
            case TipoCuenta.CuentaSueldo cs -> {
                if (cuenta.saldo().compareTo(SALDO_MINIMO_SUELDO) < 0) {
                    motivos.add("Una cuenta sueldo no admite saldo negativo");
                }
                if (normalizarCuit(cs.cuitEmpleador()).endsWith(digitosTitular(cuenta.titular()))) {
                    motivos.add("El CUIT del empleador no puede coincidir con el del titular");
                }
                if ("SIN_CONVENIO".equals(cs.convenio())) {
                    motivos.add("La cuenta sueldo debe estar asociada a un convenio de acreditación");
                }
            }
        }

        return motivos.isEmpty()
                ? new ResultadoValidacion.Valida(cuenta)
                : new ResultadoValidacion.Invalida(cuenta, motivos);
    }

    /** Descripción corta del tipo, también resuelta por pattern matching. */
    public String describir(CuentaBancaria cuenta) {
        return switch (cuenta.tipo()) {
            case TipoCuenta.CajaAhorro ca when ca.monedaExtranjera() -> "Caja de ahorro en USD";
            case TipoCuenta.CajaAhorro ca -> "Caja de ahorro en ARS (mínimo " + ca.saldoMinimo() + ")";
            case TipoCuenta.CuentaCorriente cc -> "Cuenta corriente de " + cc.cuit()
                    + " con descubierto hasta " + cc.limiteDescubierto();
            case TipoCuenta.CuentaSueldo cs -> "Cuenta sueldo bajo convenio " + cs.convenio();
        };
    }

    private static String normalizarCuit(String cuit) { return cuit.replace("-", ""); }

    private static String digitosTitular(String titular) {
        return String.valueOf(Math.abs(titular.hashCode() % 100000000)); // placeholder de padrón
    }
}