package fintech.cuentas.domain.model;

// ─────────────────────────────────────────────
// CuentaBancaria.java
// ─────────────────────────────────────────────

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Invariantes estructurales en el constructor compacto:
 * si el record existe, el CBU es sintáctica y aritméticamente válido.
 * Las reglas de negocio por tipo quedan fuera, en ValidadorCuenta.
 */
public record CuentaBancaria(String cbu, String titular, BigDecimal saldo, TipoCuenta tipo) {

    private static final int[] PESOS_BLOQUE_1 = {7, 1, 3, 9, 7, 1, 3};
    private static final int[] PESOS_BLOQUE_2 = {3, 9, 7, 1, 3, 9, 7, 1, 3, 9, 7, 1, 3};

    public CuentaBancaria {
        Objects.requireNonNull(cbu, "cbu no puede ser null");
        Objects.requireNonNull(titular, "titular no puede ser null");
        Objects.requireNonNull(saldo, "saldo no puede ser null");
        Objects.requireNonNull(tipo, "tipo no puede ser null");

        cbu = cbu.replaceAll("[\\s-]", "");
        if (!cbu.matches("\\d{22}")) {
            throw new IllegalArgumentException("El CBU debe tener exactamente 22 dígitos numéricos");
        }
        if (!digitoVerificadorValido(cbu.substring(0, 8), PESOS_BLOQUE_1)) {
            throw new IllegalArgumentException("Dígito verificador inválido en el bloque 1 del CBU");
        }
        if (!digitoVerificadorValido(cbu.substring(8), PESOS_BLOQUE_2)) {
            throw new IllegalArgumentException("Dígito verificador inválido en el bloque 2 del CBU");
        }

        titular = titular.strip();
        if (titular.length() < 3 || titular.length() > 60) {
            throw new IllegalArgumentException("El titular debe tener entre 3 y 60 caracteres");
        }
        if (!titular.matches("[\\p{L}\\p{M}'.\\s-]+")) {
            throw new IllegalArgumentException("El titular contiene caracteres no permitidos: " + titular);
        }

        if (saldo.scale() > 2) {
            throw new IllegalArgumentException("El saldo no puede tener más de 2 decimales");
        }
        saldo = saldo.stripTrailingZeros().scale() < 2 ? saldo.setScale(2) : saldo;
    }

    /** Verifica el último dígito del bloque contra la suma ponderada de los anteriores. */
    private static boolean digitoVerificadorValido(String bloque, int[] pesos) {
        int suma = 0;
        for (int i = 0; i < pesos.length; i++) {
            suma += Character.getNumericValue(bloque.charAt(i)) * pesos[i];
        }
        int esperado = (10 - (suma % 10)) % 10;
        return esperado == Character.getNumericValue(bloque.charAt(pesos.length));
    }

    public String entidad()  { return cbu.substring(0, 3); }
    public String sucursal() { return cbu.substring(3, 7); }

    /** CBU formateado para mostrar en UI: 8 dígitos - 14 dígitos. */
    public String cbuFormateado() { return cbu.substring(0, 8) + "-" + cbu.substring(8); }
}