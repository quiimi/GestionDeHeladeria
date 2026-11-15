package modelo;

import java.util.List;

/**
 * Representa un producto de tipo cono de helado.
 * Hereda los atributos generales de Producto y agrega
 * la cantidad de bochas y los sabores seleccionados.
 */
public class ConoHelado extends Producto {
    private int cantBochas;
    private List<SaborHelado> sabores;

    /**
     * Crea un cono con su información, cantidad de bochas y sabores.
     */
    public ConoHelado(int id, String nombre, double precioBase,
                      int cantBochas, List<SaborHelado> sabores) {
        super(id, nombre, precioBase);
        this.cantBochas = cantBochas;
        this.sabores = sabores;
    }

    /**
     * Calcula el precio del cono multiplicando el precio base
     * por la cantidad de bochas.
     */
    @Override
    public double calcularPrecio() {
        return precioBase * cantBochas;
    }

    /** Devuelve la cantidad de bochas del cono. */
    public int getCantBochas() { return cantBochas; }

    /** Devuelve la lista de sabores seleccionados. */
    public List<SaborHelado> getSabores() { return sabores; }
}
