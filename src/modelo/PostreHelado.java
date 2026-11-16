package modelo;

/**
 * Representa un postre helado que se vende por unidad.
 * Hereda los atributos generales de Producto y administra
 * su tipo y la cantidad disponible en stock.
 */
public class PostreHelado extends Producto {

    private TipoPostre tipoPostre;
    private int stock;

    /**
     * Crea un postre con su información, tipo y stock inicial.
     */
    public PostreHelado(int id, String nombre, double precioBase,
                        TipoPostre tipoPostre, int stock) {
        super(id, nombre, precioBase);
        this.tipoPostre = tipoPostre;
        this.stock = stock;
    }

    /**
     * Devuelve el precio base porque el postre se vende por unidad.
     */
    @Override
    public double calcularPrecio() {
        return precioBase;
    }

    /** Devuelve el tipo de postre. */
    public TipoPostre getTipoPostre() {
        return tipoPostre;
    }

    /** Devuelve la cantidad de unidades disponibles. */
    public int getStock() {
        return stock;
    }

    /**
     * Actualiza el stock, evitando que se establezca un valor negativo.
     */
    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    /**
     * Descuenta unidades del stock después de una venta.
     * Verifica que la cantidad sea válida y que exista stock suficiente.
     */
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

    /**
     * Incrementa el stock cuando se incorporan nuevas unidades.
     * No permite reponer cantidades menores o iguales a cero.
     */
    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }

        stock += cantidad;
    }
}