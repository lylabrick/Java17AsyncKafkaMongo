package fintech;

// ─────────────────────────────────────────────
// Demo.java
// ─────────────────────────────────────────────

import fintech.cuentas.domain.model.CuentaBancaria;
import fintech.cuentas.domain.model.TipoCuenta;
import fintech.cuentas.domain.validation.ResultadoValidacion;
import fintech.cuentas.domain.validation.ValidadorCuenta;

import java.math.BigDecimal;

public class Demo {
    public static void main(String[] args) {
        ValidadorCuenta validador = new ValidadorCuenta();

        var caja = new CuentaBancaria(
                "0170099220000067797172",
                "Laura Gómez",
                new BigDecimal("125000.50"),
                new TipoCuenta.CajaAhorro(new BigDecimal("1000.00"), false));

        var corriente = new CuentaBancaria(
                "0170099220000067797172",
                "Distribuidora del Sur SRL",
                new BigDecimal("-480000.00"),
                new TipoCuenta.CuentaCorriente(new BigDecimal("300000.00"), "30-71234567-4"));

        for (var cuenta : java.util.List.of(caja, corriente)) {
            System.out.println(validador.describir(cuenta));
            System.out.println("  " + switch (validador.validar(cuenta)) {
                case ResultadoValidacion.Valida v -> "OK: " + v.cuenta().cbuFormateado();
                case ResultadoValidacion.Invalida i -> "RECHAZADA: " + String.join(" | ", i.motivos());
            });
        }
    }
}
