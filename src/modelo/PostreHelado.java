package modelo;

public class PostreHelado extends Producto {
    private TipoPostre tipoPostre;

    public PostreHelado(int id, String nombre, double precioBase, TipoPostre tipoPostre) {
        super(id, nombre, precioBase);
        this.tipoPostre = tipoPostre;
    }

    @Override
    public double calcularPrecio() {
        return precioBase;
    }

    public TipoPostre getTipoPostre() { return tipoPostre; }
}