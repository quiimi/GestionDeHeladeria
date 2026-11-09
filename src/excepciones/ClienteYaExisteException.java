package excepciones;

public class ClienteYaExisteException extends Exception {
    public ClienteYaExisteException(String mensaje) {
        super(mensaje);
    }
}
