package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una reserva de catering para un evento.
 * Almacena los datos del cliente, cajero, fecha, cantidad de personas
 * y los importes correspondientes a la seña y al total.
 */
public class Catering {
    private int id;
    private Cliente cliente;
    private Cajero cajero;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaEvento;
    private int cantPersonas;
    private double montoSenia;
    private double montoTotal;
    private String estado;

    /**
     * Crea una reserva de catering con todos sus datos.
     * Registra automáticamente la fecha actual y establece
     * el estado inicial de la reserva como CONFIRMADA.
     */
    public Catering(Cliente cliente, Cajero cajero, LocalDateTime fechaEvento,
                    int cantPersonas, double montoSenia, double montoTotal) {
        this.cliente = cliente;
        this.cajero = cajero;
        this.fechaRegistro = LocalDateTime.now();
        this.fechaEvento = fechaEvento;
        this.cantPersonas = cantPersonas;
        this.montoSenia = montoSenia;
        this.montoTotal = montoTotal;
        this.estado = "CONFIRMADA";
    }

    /**
     * Constructor alternativo que permite crear una reserva
     * sin especificar un cajero.
     */
    public Catering(Cliente cliente, LocalDateTime fechaEvento,
                    int cantPersonas, double montoSenia, double montoTotal) {
        this(cliente, null, fechaEvento, cantPersonas, montoSenia, montoTotal);
    }

    /**
     * Constructor utilizado para reconstruir una reserva existente
     * con su identificador y estado almacenados.
     */
    public Catering(int id, Cliente cliente, LocalDateTime fechaEvento,
                    int cantPersonas, double montoSenia, double montoTotal, String estado) {
        this(cliente, null, fechaEvento, cantPersonas, montoSenia, montoTotal);
        this.id = id;
        this.estado = estado;
    }

    /**
     * Calcula el saldo que todavía debe pagar el cliente,
     * restando la seña al monto total del evento.
     */
    public double calcularSaldoPendiente() {
        return montoTotal - montoSenia;
    }

    /** Devuelve el importe de la seña abonada. */
    public double getSenia() {
        return montoSenia;
    }

    /** Devuelve el importe total del catering. */
    public double getTotal() {
        return montoTotal;
    }

    /** Devuelve el saldo pendiente de pago. */
    public double getSaldoPendiente() {
        return calcularSaldoPendiente();
    }

    /** Devuelve el identificador de la reserva. */
    public int getId() { return id; }

    /** Modifica el identificador de la reserva. */
    public void setId(int id) { this.id = id; }

    /** Devuelve el cliente asociado a la reserva. */
    public Cliente getCliente() { return cliente; }

    /** Modifica el cliente asociado a la reserva. */
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    /** Devuelve el cajero que registró la reserva. */
    public Cajero getCajero() { return cajero; }

    /** Modifica el cajero asociado a la reserva. */
    public void setCajero(Cajero cajero) { this.cajero = cajero; }

    /** Devuelve la fecha en que se registró la reserva. */
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    /** Modifica la fecha de registro de la reserva. */
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    /** Devuelve la fecha y hora del evento. */
    public LocalDateTime getFechaEvento() { return fechaEvento; }

    /** Modifica la fecha y hora del evento. */
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }

    /** Devuelve la cantidad de personas del evento. */
    public int getCantPersonas() { return cantPersonas; }

    /** Modifica la cantidad de personas del evento. */
    public void setCantPersonas(int cantPersonas) { this.cantPersonas = cantPersonas; }

    /** Devuelve el importe de la seña. */
    public double getMontoSenia() { return montoSenia; }

    /** Modifica el importe de la seña. */
    public void setMontoSenia(double montoSenia) { this.montoSenia = montoSenia; }

    /** Devuelve el monto total del evento. */
    public double getMontoTotal() { return montoTotal; }

    /** Modifica el monto total del evento. */
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    /** Devuelve el estado actual de la reserva. */
    public String getEstado() { return estado; }

    /** Modifica el estado de la reserva. */
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * Genera una representación textual de la reserva,
     * mostrando sus datos principales y el saldo pendiente.
     * Formatea la fecha del evento y contempla datos nulos.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String fechaFormateada = (fechaEvento != null)
                ? fechaEvento.format(formatter) : "N/A";

        return "Catering ID: " + id
                + " | Cliente: " + (cliente != null ? cliente.getNombre() + " (DNI: " + cliente.getDni() + ")" : "N/A")
                + " | Fecha Evento: " + fechaFormateada
                + " | Personas: " + cantPersonas
                + " | Seña: $" + montoSenia
                + " | Total: $" + montoTotal
                + " | Saldo Pendiente: $" + calcularSaldoPendiente()
                + " | Estado: " + estado;
    }
}