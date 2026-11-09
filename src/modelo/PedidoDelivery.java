package modelo;

import excepciones.DistanciaEntregaExcedidaException;

public class PedidoDelivery extends Pedido{
    private String direccionEntrega;
    private double distanciaKm;

    public PedidoDelivery(Cliente cliente, Cajero cajero, String direccionEntrega, double distanciaKm) throws DistanciaEntregaExcedidaException {
        super(cliente, cajero);
        if (distanciaKm > 15.0) {
            throw new DistanciaEntregaExcedidaException("ERROR: La distancia de " + distanciaKm + " km supera el limite de entrega (15 km).");
        }
        this.direccionEntrega = direccionEntrega;
        this.distanciaKm = distanciaKm;
    }

    @Override
    public double calcularMontoTotal() {
        return super.calcularMontoTotal() + (distanciaKm * 300.0);
    }

    @Override
    public String getTipoPedido() {
        return "DELIVERY";
    }

    public String getDireccionEntrega() { return direccionEntrega; }
    public double getDistanciaKm() { return distanciaKm; }

}
