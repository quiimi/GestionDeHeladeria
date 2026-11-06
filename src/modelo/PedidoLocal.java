package modelo;

public class PedidoLocal extends Pedido {
    private int numeroMesa;

    public PedidoLocal(Cliente cliente, Cajero cajero, int numeroMesa) {
        super(cliente, cajero);
        this.numeroMesa = numeroMesa;
    }

    @Override
    public String getTipoPedido() {
        return "LOCAL";
    }

    public int getNumeroMesa() { return numeroMesa; }
}