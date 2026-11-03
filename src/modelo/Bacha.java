package modelo;

import excepciones.BachaLlenaException;
import excepciones.BachaVaciaException;

public class Bacha<T extends SaborHelado> {
    private T sabor;
    private double capacidadMaxKg;
    private double cantidadActualKg;

    public Bacha(T sabor, double capacidadMaxKg) {
        this.sabor = sabor;
        this.capacidadMaxKg = capacidadMaxKg;
        this.cantidadActualKg = capacidadMaxKg;
    }

    public void agregarHelado(double cantidadKg) throws BachaLlenaException {
        if (cantidadActualKg + cantidadKg > capacidadMaxKg) {
            throw new BachaLlenaException("ERROR: La bacha de " + sabor.getNombre() + " supera la capacidad maxima (" + capacidadMaxKg + " kg).");
        }
        cantidadActualKg += cantidadKg;
        if (sabor.getEstado() == EstadoSabor.SIN_STOCK) {
            sabor.setEstado(EstadoSabor.DISPONIBLE);
        }
    }

    public void eliminarHelado(double cantidadKg) throws BachaVaciaException {
        if (cantidadActualKg - cantidadKg < 0) {
            throw new BachaVaciaException("ERROR: No hay suficiente helado en la bacha de " + sabor.getNombre() + ". Quedan " + cantidadActualKg + " kg.");
        }
        cantidadActualKg -= cantidadKg;
        if (cantidadActualKg == 0) {
            sabor.setEstado(EstadoSabor.SIN_STOCK);
        }
    }

    public T getSabor() { return sabor; }
    public double getCapacidadMaxKg() { return capacidadMaxKg; }
    public double getCantidadActualKg() { return cantidadActualKg; }
}