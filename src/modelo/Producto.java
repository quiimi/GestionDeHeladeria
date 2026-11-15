package modelo;

/**
 * Clase abstracta que representa un producto vendido en la heladería.
 * Implementa CalculablePrecio para establecer que sus productos*/
 public abstract class Producto implements CalculablePrecio {
 protected int id;
 protected String nombre;
 protected double precioBase;

 // Inicializa los atributos comunes de cualquier producto.

public Producto(int id, String nombre, double precioBase) {
    this.id = id;
    this.nombre = nombre;
    this.precioBase = precioBase;
}

/** Devuelve el identificador del producto. */
public int getId() { return id; }

/** Modifica el identificador del producto. */
public void setId(int id) { this.id = id; }

/** Devuelve el nombre del producto. */
public String getNombre() { return nombre; }

/** Modifica el nombre del producto. */
public void setNombre(String nombre) { this.nombre = nombre; }

/** Devuelve el precio base del producto. */
public double getPrecioBase() { return precioBase; }

/** Modifica el precio base del producto. */
public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
}
