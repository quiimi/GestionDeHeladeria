package modelo;

import excepciones.DistanciaEntregaExcedidaException;

/**
 * Representa un pedido que debe entregarse a domicilio.
 * Hereda los datos y métodos comunes de la clase Pedido.
 */
public class PedidoDelivery extends Pedido {
    private String direccionEntrega;
    private double distanciaKm;

    /**
     * Crea un pedido delivery y verifica que la distancia
     * de entrega no supere el límite permitido de 15 kilómetros.
     */
    public PedidoDelivery(Cliente cliente, Cajero cajero,
                          String direccionEntrega, double distanciaKm)
            throws DistanciaEntregaExcedidaException {
        super(cliente, cajero);
        if (distanciaKm > 15.0) {
            throw new DistanciaEntregaExcedidaException("ERROR: La distancia de " + distanciaKm + " km supera el limite de entrega (15 km).");
        }
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
    }

    /**
     * Calcula el total del pedido sumando el precio de los productos
     * y el costo de envío, que depende de la distancia.
     */
    @Override
    public double calcularMontoTotal() {
        return super.calcularMontoTotal() + (distanciaKm * 300.0);
    }

    /**
     * Devuelve el tipo de pedido correspondiente a delivery.
     */
    @Override
    public String getTipoPedido() {
        return "DELIVERY";
    }

    /** Devuelve la dirección donde debe entregarse el pedido. */
    public String getDireccionEntrega() { return direccionEntrega; }

    /** Devuelve la distancia de entrega en kilómetros. */
    public double getDistanciaKm() { return distanciaKm; }
}
