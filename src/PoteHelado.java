import java.util.List;

public class PoteHelado extends Producto {
    private double kilos;
    private List<SaborHelado> sabores;

    public PoteHelado(int id, String nombre, double precioBase, double kilos, List<SaborHelado> sabores) {
        super(id, nombre, precioBase);
        this.kilos = kilos;
        this.sabores = sabores;
    }

    @Override
    public double calcularPrecio() {
        return precioBase * kilos;
    }

    public double getKilos() { return kilos; }
    public List<SaborHelado> getSabores() { return sabores; }
}