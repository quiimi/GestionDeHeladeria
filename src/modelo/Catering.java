
package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // Constructor anterior, mantenido por compatibilidad
    public Catering(Cliente cliente, LocalDateTime fechaEvento,
                    int cantPersonas, double montoSenia, double montoTotal) {
        this(cliente, null, fechaEvento, cantPersonas, montoSenia, montoTotal);
    }

    public Catering(int id, Cliente cliente, LocalDateTime fechaEvento,
                    int cantPersonas, double montoSenia, double montoTotal, String estado) {
        this(cliente, null, fechaEvento, cantPersonas, montoSenia, montoTotal);
        this.id = id;
        this.estado = estado;
    }

    public double calcularSaldoPendiente() {
        return montoTotal - montoSenia;
    }

    public double getSenia() {
        return montoSenia;
    }

    public double getTotal() {
        return montoTotal;
    }

    public double getSaldoPendiente() {
        return calcularSaldoPendiente();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Cajero getCajero() { return cajero; }
    public void setCajero(Cajero cajero) { this.cajero = cajero; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }

    public int getCantPersonas() { return cantPersonas; }
    public void setCantPersonas(int cantPersonas) { this.cantPersonas = cantPersonas; }

    public double getMontoSenia() { return montoSenia; }
    public void setMontoSenia(double montoSenia) { this.montoSenia = montoSenia; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

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
