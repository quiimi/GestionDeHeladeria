
package modelo;

public class PostreHelado extends Producto {

    private TipoPostre tipoPostre;
    private int stock;

    public PostreHelado(int id, String nombre, double precioBase,
                        TipoPostre tipoPostre, int stock) {
        super(id, nombre, precioBase);
        this.tipoPostre = tipoPostre;
        this.stock = stock;
    }

    @Override
    public double calcularPrecio() {
        return precioBase;
    }

    public TipoPostre getTipoPostre() {
        return tipoPostre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        if (cantidad > stock) {
            throw new IllegalArgumentException(
                    "Stock insuficiente de " + nombre + ". Disponible: " + stock
            );
        }

        stock -= cantidad;
    }

    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        stock += cantidad;
    }
}
