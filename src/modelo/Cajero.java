package modelo;

import excepciones.SaborNoDisponibleException;
import java.util.List;

public class Cajero extends Usuario {
    private boolean cajaAbierta;
    private double totalRecaudado;
    private int cantPedidosLocal;
    private int cantPedidosDelivery;
    private int cantCaterings;

    public Cajero(String dni, String nombre, String username) {
        super(dni, nombre, username);
        this.cajaAbierta = false;
        this.totalRecaudado = 0.0;
        this.cantPedidosLocal = 0;
        this.cantPedidosDelivery = 0;
        this.cantCaterings = 0;
    }

    public boolean isCajaAbierta() {
        return cajaAbierta;
    }

    public void abrirCaja() {
        this.cajaAbierta = true;
        this.totalRecaudado = 0.0;
        this.cantPedidosLocal = 0;
        this.cantPedidosDelivery = 0;
        this.cantCaterings = 0;
        System.out.println("Caja abierta por " + nombre + ".");
    }

    public void registrarVenta(double monto, String tipo) {
        if (!cajaAbierta) {
            throw new IllegalStateException("ERROR: La caja esta cerrada.");
        }
        this.totalRecaudado += monto;
        if (tipo.equalsIgnoreCase("LOCAL")) {
            cantPedidosLocal++;
        } else if (tipo.equalsIgnoreCase("DELIVERY")) {
            cantPedidosDelivery++;
        } else if (tipo.equalsIgnoreCase("CATERING")) {
            cantCaterings++;
        }
    }

    public void cerrarCaja() {
        if (!cajaAbierta) {
            System.out.println("ERROR: La caja ya se encuentra cerrada.");
            return;
        }
        this.cajaAbierta = false;
        System.out.println("\n--- RESUMEN DE CIERRE DE CAJA ---");
        System.out.println("Cajero a cargo: " + nombre);
        System.out.println("Pedidos Locales: " + cantPedidosLocal);
        System.out.println("Pedidos Delivery: " + cantPedidosDelivery);
        System.out.println("Reservas Catering: " + cantCaterings);
        System.out.println("Total Recaudado: $" + totalRecaudado);
        System.out.println("Caja cerrada exitosamente.");
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
    }
}
