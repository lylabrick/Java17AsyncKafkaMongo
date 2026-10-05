package reportes.servicio;

import reportes.modelo.FilaReporte;
import reportes.repositorio.ReporteRepositorio;

import java.time.LocalDate;
import java.util.List;

public class ServicioReportes {

    private final ReporteRepositorio repositorio;

    public ServicioReportes(ReporteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public String generarReporteVentas(LocalDate desde, LocalDate hasta) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("'desde' no puede ser posterior a 'hasta'");
        }

        var filas = repositorio.ventasPorCliente(desde, hasta);
        return formatear(desde, hasta, filas);
    }

    private String formatear(LocalDate desde, LocalDate hasta, List<FilaReporte> filas) {
        var sb = new StringBuilder();
        sb.append("""
                Reporte de ventas por cliente
                Período: %s a %s
                -----------------------------------------------
                """.formatted(desde, hasta));

        for (var fila : filas) {
            sb.append("%-25s %5d pedidos  $ %,12.2f%n"
                    .formatted(fila.cliente(), fila.cantidadPedidos(), fila.totalFacturado()));
        }
        return sb.toString();
    }
}