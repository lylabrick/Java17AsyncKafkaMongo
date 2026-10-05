package reportes.repositorio;

import reportes.config.ConfiguracionBD;
import reportes.modelo.FilaReporte;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteRepositorio {

    private final ConfiguracionBD config;

    public ReporteRepositorio(ConfiguracionBD config) {
        this.config = config;
    }

    public List<FilaReporte> ventasPorCliente(LocalDate desde, LocalDate hasta) {
        var sql = """
                SELECT c.nombre                AS cliente,
                       COUNT(p.id)             AS cantidad_pedidos,
                       COALESCE(SUM(p.total), 0) AS total_facturado
                FROM   %1$s.clientes c
                JOIN   %1$s.pedidos  p ON p.cliente_id = c.id
                WHERE  p.fecha BETWEEN ? AND ?
                GROUP  BY c.nombre
                ORDER  BY total_facturado DESC
                """.formatted(config.esquema());

        var filas = new ArrayList<FilaReporte>();

        try (var conexion = DriverManager.getConnection(
                config.urlJdbc(), config.usuario(), config.contrasenia());
             var sentencia = conexion.prepareStatement(sql)) {

            sentencia.setDate(1, Date.valueOf(desde));
            sentencia.setDate(2, Date.valueOf(hasta));

            try (var rs = sentencia.executeQuery()) {
                while (rs.next()) {
                    var fila = new FilaReporte(
                            rs.getString("cliente"),
                            rs.getInt("cantidad_pedidos"),
                            rs.getBigDecimal("total_facturado"));
                    filas.add(fila);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al ejecutar el reporte de ventas", e);
        }

        return filas;
    }
}