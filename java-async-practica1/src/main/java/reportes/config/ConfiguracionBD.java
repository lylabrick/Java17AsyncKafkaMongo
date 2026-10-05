package reportes.config;

public record ConfiguracionBD(String host, int puerto, String usuario,
                              String contrasenia, String esquema) {

    public ConfiguracionBD {
        if (!esquema.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Nombre de esquema inválido: " + esquema);
        }
    }

    public String urlJdbc() {
        return "jdbc:postgresql://%s:%d/postgres".formatted(host, puerto);
    }
}