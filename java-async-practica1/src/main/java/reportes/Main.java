package reportes;

import reportes.config.CargadorConfiguracion;
import reportes.repositorio.ReporteRepositorio;
import reportes.servicio.ServicioReportes;

import java.nio.file.Path;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        var ruta = Path.of(args.length > 0 ? args[0] : "config.properties");

        var config = new CargadorConfiguracion().cargar(ruta);
        var repositorio = new ReporteRepositorio(config);
        var servicio = new ServicioReportes(repositorio);

        var desde = LocalDate.now().minusMonths(1);
        var hasta = LocalDate.now();

        System.out.println(servicio.generarReporteVentas(desde, hasta));
    }
}