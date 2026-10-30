import java.time.LocalDateTime;

public class Catering {
    private int id;
    private Cliente cliente;
    private LocalDateTime fechaEvento;
    private int cantPersonas;
    private double montoSenia;
    private double montoTotal;
    private String estado;

    public Catering(Cliente cliente, LocalDateTime fechaEvento, int cantPersonas, double montoSenia, double montoTotal) {
        this.cliente = cliente;
        this.fechaEvento = fechaEvento;
        this.cantPersonas = cantPersonas;
        this.montoSenia = montoSenia;
        this.montoTotal = montoTotal;
        this.estado = "CONFIRMADA";
    }

    public Catering(int id, Cliente cliente, LocalDateTime fechaEvento, int cantPersonas, double montoSenia, double montoTotal, String estado) {
        this(cliente, fechaEvento, cantPersonas, montoSenia, montoTotal);
        this.id = id;
        this.estado = estado;
    }

    public double calcularSaldoPendiente() {
        return montoTotal - montoSenia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public int getCantPersonas() { return cantPersonas; }
    public double getMontoSenia() { return montoSenia; }
    public double getMontoTotal() { return montoTotal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}