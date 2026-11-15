package modelo;

/**
 * Representa un pedido realizado para consumir dentro del local.
 * Hereda los atributos y comportamientos comunes de Pedido.
 */
public class PedidoLocal extends Pedido {
    private int numeroMesa;

    /**
     * Crea un pedido local asociado a un cliente, un cajero y una mesa.
     */
    public PedidoLocal(Cliente cliente, Cajero cajero, int numeroMesa) {
        super(cliente, cajero);
        this.numeroMesa = numeroMesa;
    }

    /**
     * Devuelve el tipo de pedido correspondiente al consumo local.
     */
    @Override
    public String getTipoPedido() {
        return "LOCAL";
    }

    /** Devuelve el número de mesa asignado al pedido. */
    public int getNumeroMesa() { return numeroMesa; }
}