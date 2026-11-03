package modelo;
import java.util.List;

public class Cajero extends Usuario {
    private boolean cajaAbierta;

    public Cajero(String dni, String nombre, String username) {
        super(dni, nombre, username);
        this.cajaAbierta = false;
    }

    public boolean isCajaAbierta() { return cajaAbierta; }

    public void abrirCaja() {
        this.cajaAbierta = true;
        System.out.println("Caja abierta por " + nombre);
    }

    public void cerrarCaja() {
        this.cajaAbierta = false;
        System.out.println("Caja cerrada por " + nombre);
    }

    public void tomarPedido(List<SaborHelado> saboresSolicitados) throws SaborNoDisponibleException {
        if (!cajaAbierta) {
            throw new IllegalStateException("ERROR: No se pueden tomar pedidos con la caja cerrada.");
        }

        for (SaborHelado sabor : saboresSolicitados) {
            if (sabor.getEstado() != EstadoSabor.DISPONIBLE) {
                throw new SaborNoDisponibleException("ERROR: El sabor '" + sabor.getNombre() + "' no esta disponible (" + sabor.getEstado() + ").");
            }
        }
        System.out.println("Pedido procesado exitosamente por " + nombre);
    }
}