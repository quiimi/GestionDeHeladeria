import java.util.List;

public class ConoHelado extends Producto {
    private int cantBochas;
    private List<SaborHelado> sabores;

    public ConoHelado(int id, String nombre, double precioBase, int cantBochas, List<SaborHelado> sabores) {
        super(id, nombre, precioBase);
        this.cantBochas = cantBochas;
        this.sabores = sabores;
    }

    @Override
    public double calcularPrecio() {
        return precioBase + (cantBochas * 500.0);
    }

    public int getCantBochas() { return cantBochas; }
    public List<SaborHelado> getSabores() { return sabores; }
}
