package modelo;

import excepciones.BachaLlenaException;
import excepciones.BachaVaciaException;

/**
 * Representa una bacha que almacena helado de un sabor determinado.
 * Utiliza generics para trabajar con objetos que hereden de SaborHelado.
 */
public class Bacha<T extends SaborHelado> {
    private T sabor;
    private double capacidadMaxKg;
    private double cantidadActualKg;

    /**
     * Crea una bacha asociada a un sabor y una capacidad máxima.
     * Inicialmente establece la cantidad actual igual a su capacidad.
     */
    public Bacha(T sabor, double capacidadMaxKg) {
        this.sabor = sabor;
        this.capacidadMaxKg = capacidadMaxKg;
        this.cantidadActualKg = capacidadMaxKg;
    }

    /**
     * Agrega kilos de helado a la bacha.
     * Si se supera la capacidad máxima, lanza una excepción.
     * Si el sabor estaba sin stock, vuelve a marcarlo como disponible.
     */
    public void agregarHelado(double cantidadKg) throws BachaLlenaException {
        if (cantidadActualKg + cantidadKg > capacidadMaxKg) {
            throw new BachaLlenaException("ERROR: La bacha de " + sabor.getNombre() + " supera la capacidad maxima (" + capacidadMaxKg + " kg).");
        }
        cantidadActualKg += cantidadKg;
        if (sabor.getEstado() == EstadoSabor.SIN_STOCK) {
            sabor.setEstado(EstadoSabor.DISPONIBLE);
        }
    }

    /**
     * Retira kilos de helado de la bacha.
     * Si no hay suficiente cantidad, lanza una excepción.
     * Si queda vacía, actualiza el estado del sabor a SIN_STOCK.
     */
    public void eliminarHelado(double cantidadKg) throws BachaVaciaException {
        if (cantidadActualKg - cantidadKg < 0) {
            throw new BachaVaciaException("ERROR: No hay suficiente helado en la bacha de " + sabor.getNombre() + ". Quedan " + cantidadActualKg + " kg.");
        }
        cantidadActualKg -= cantidadKg;
        if (cantidadActualKg == 0) {
            sabor.setEstado(EstadoSabor.SIN_STOCK);
        }
    }

    /** Devuelve el sabor almacenado en la bacha. */
    public T getSabor() { return sabor; }

    /** Devuelve la capacidad máxima de la bacha en kilos. */
    public double getCapacidadMaxKg() { return capacidadMaxKg; }

    /** Devuelve la cantidad actual de helado en kilos. */
    public double getCantidadActualKg() { return cantidadActualKg; }
}