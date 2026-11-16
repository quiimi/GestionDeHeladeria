package modelo;

import java.util.List;

/**
 * Representa un pote de helado vendido por peso.
 * Hereda los atributos generales de Producto y agrega
 * los kilos y los sabores seleccionados.
 */
public class PoteHelado extends Producto {
    private double kilos;
    private List<SaborHelado> sabores;

    /**
     * Crea un pote con su información, peso y sabores seleccionados.
     */
    public PoteHelado(int id, String nombre, double precioBase,
                      double kilos, List<SaborHelado> sabores) {
        super(id, nombre, precioBase);
        this.kilos = kilos;
        this.sabores = sabores;
    }

    /**
     * Calcula el precio del pote multiplicando el precio por kilo
     * por la cantidad de kilos solicitados.
     */
    @Override
    public double calcularPrecio() {
        return precioBase * kilos;
    }

    /** Devuelve el peso del pote en kilos. */
    public double getKilos() { return kilos; }

    /** Devuelve los sabores incluidos en el pote. */
    public List<SaborHelado> getSabores() { return sabores; }
}