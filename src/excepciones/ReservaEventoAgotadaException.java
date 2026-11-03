package excepciones;

public class ReservaEventoAgotadaException extends Exception{
    public ReservaEventoAgotadaException(String message) {
        super(message);
    }
}