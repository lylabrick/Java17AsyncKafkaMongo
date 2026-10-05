package reportes.modelo;

import java.math.BigDecimal;

public record FilaReporte(String cliente, int cantidadPedidos, BigDecimal totalFacturado) {
}