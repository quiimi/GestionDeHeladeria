public class PostreHelado extends Producto {
    private String tipoPostre;

    public PostreHelado(int id, String nombre, double precioBase, String tipoPostre) {
        super(id, nombre, precioBase);
        this.tipoPostre = tipoPostre;
    }

    @Override
    public double calcularPrecio() {
        return precioBase;
    }

    public String getTipoPostre() { return tipoPostre; }
}