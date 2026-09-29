package logistica.envios;

// ─────────────────────────────────────────────
// Main.java
// ─────────────────────────────────────────────

import java.time.LocalDateTime;

public class Demo {
    public static void main(String[] args) {
        CalculadoraDemora calculadora = new CalculadoraDemora();

        LocalDateTime creacion  = LocalDateTime.of(2026, 9, 20, 9, 0);
        LocalDateTime despacho  = LocalDateTime.of(2026, 9, 21, 14, 0);
        LocalDateTime estimada  = LocalDateTime.of(2026, 9, 24, 12, 0);

        Envio preparacion = new Envio.EnPreparacion(
                "ENV-0001", creacion, "Depósito CABA Norte");

        Envio transito = new Envio.EnTransito(
                "ENV-0002", creacion, despacho, "Andreani", "Rosario");

        Envio entregadoTarde = new Envio.Entregado(
                "ENV-0003", creacion, despacho,
                LocalDateTime.of(2026, 9, 26, 10, 0), // entrega real
                "Juan Pérez",
                estimada);

        Envio entregadoTemprano = new Envio.Entregado(
                "ENV-0004", creacion, despacho,
                LocalDateTime.of(2026, 9, 23, 8, 0), // entrega real
                "Marta Díaz",
                estimada);

        Envio devuelto = new Envio.Devuelto(
                "ENV-0005", creacion, despacho,
                LocalDateTime.of(2026, 9, 25, 17, 0),
                "Domicilio inexistente",
                estimada);

        for (Envio e : new Envio[]{preparacion, transito, entregadoTarde, entregadoTemprano, devuelto}) {
            System.out.printf("%-10s [%-14s] -> %s%n",
                    e.codigoSeguimiento(),
                    e.getClass().getSimpleName(),
                    calculadora.describirDemora(e));
        }

        // Caso que dispara EstadoEnvioException si se aplicó el cambio sugerido antes
        try {
            new Envio.EnTransito(
                    "ENV-ERR", creacion,
                    creacion.minusDays(1), // despacho antes que creación: inconsistente
                    "OCA", "Córdoba");
        } catch (EstadoEnvioException ex) {
            System.out.println("Error esperado: " + ex.getMessage());
        }
    }
}