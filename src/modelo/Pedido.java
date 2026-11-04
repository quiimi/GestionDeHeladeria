package modelo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Pedido {
    protected int id;
    protected Cliente cliente;
    protected Cajero cajero;
    protected LocalDateTime fechaHora;
    protected List<Producto> productos;

    public Pedido(Cliente cliente, Cajero cajero) {
        this.cliente = cliente;
        this.cajero = cajero;
        this.fechaHora = LocalDateTime.now();
        this.productos = new ArrayList<>();
    }

    public void agregarProducto(Producto p) {
        productos.add(p);
    }

    public double calcularMontoTotal() {
        double total = 0;
        for (Producto p : productos) {
            total += p.calcularPrecio();
        }
        return total;
    }

    public abstract String getTipoPedido();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public Cajero getCajero() { return cajero; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public List<Producto> getProductos() { return productos; }

}
