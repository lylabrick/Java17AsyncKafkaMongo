package reportes.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Properties;

public class CargadorConfiguracion {

    public ConfiguracionBD cargar(Path ruta) {
        var props = new Properties();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ruta.toString())) {
            if (is == null) {
                throw new IllegalArgumentException("No se encontró el archivo en el classpath: " + ruta);
            }
            props.load(is);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo de configuración: " + ruta, e);
        }

        var host = requerida(props, "db.host");
        var puerto = Integer.parseInt(requerida(props, "db.puerto"));
        var usuario = requerida(props, "db.usuario");
        var contrasenia = requerida(props, "db.contrasenia");
        var esquema = requerida(props, "db.esquema");

        return new ConfiguracionBD(host, puerto, usuario, contrasenia, esquema);
    }

    private String requerida(Properties props, String clave) {
        var valor = props.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Falta la propiedad obligatoria: " + clave);
        }
        return valor.trim();
    }
}