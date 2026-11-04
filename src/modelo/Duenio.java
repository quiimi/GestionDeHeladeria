package modelo;

public class Duenio extends Usuario {
    public Duenio(String dni, String nombre, String username) {
        super(dni, nombre, username);
    }

    public void cambiarPrecio(Producto producto, double nuevoPrecioBase) {
        producto.setPrecioBase(nuevoPrecioBase);
        System.out.println("Precio de " + producto.getNombre() + " actualizado a $" + nuevoPrecioBase);
    }

    public void cambiarEstadoSabor(SaborHelado sabor, EstadoSabor nuevoEstado) {
        sabor.setEstado(nuevoEstado);
    }

}
