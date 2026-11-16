package dao;

import excepciones.BachaVaciaException;
import modelo.Bacha;
import modelo.EstadoSabor;
import modelo.SaborHelado;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO encargado de gestionar la persistencia de los productos
 * relacionados con los sabores, las bachas y el stock de postres.
 */
public class ProductoDAO {

    /**
     * Guarda un nuevo sabor en la base de datos.
     * Almacena sus características, estado y stock inicial.
     */
    public boolean guardarSabor(SaborHelado sabor, double stockInicialKg) {

        String sql = "INSERT INTO sabores "
                + "(nombre, sin_tacc, vegano, estado, capacidad_max_kg, stock_kg) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, sabor.getNombre());
            stmt.setBoolean(2, sabor.isSinTacc());
            stmt.setBoolean(3, sabor.isVegano());
            stmt.setString(4, sabor.getEstado().name());
            stmt.setDouble(5, stockInicialKg);
            stmt.setDouble(6, stockInicialKg);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en ProductoDAO.guardarSabor - "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado y la cantidad de stock de un sabor.
     * Busca el sabor sin distinguir entre mayúsculas y minúsculas.
     */
    public boolean actualizarEstadoYStockSabor(String nombreSabor, EstadoSabor estado, double stockKg) {
        String sql = "UPDATE sabores SET estado = ?, stock_kg = ? WHERE LOWER(nombre) = LOWER(?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, estado.name());
            stmt.setDouble(2, stockKg);
            stmt.setString(3, nombreSabor);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en ProductoDAO.actualizarEstadoYStockSabor - " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos los sabores almacenados y reconstruye sus bachas.
     * Conserva la capacidad máxima y el stock real registrado en la base.
     *
     * @return un HashMap cuya clave es el nombre del sabor
     * y cuyo valor es la bacha correspondiente.
     */
    public Map<String, Bacha<SaborHelado>> obtenerTodasLasBachas() {

        Map<String, Bacha<SaborHelado>> bachas = new HashMap<>();

        String sql = "SELECT * FROM sabores";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Recorre todos los sabores registrados.
            while (rs.next()) {

                String nombre = rs.getString("nombre");
                boolean sinTacc = rs.getBoolean("sin_tacc");
                boolean vegano = rs.getBoolean("vegano");

                String estadoStr = rs.getString("estado");

                double capacidadMaxKg = rs.getDouble("capacidad_max_kg");
                double stockKg = rs.getDouble("stock_kg");

                // Reconstruye el objeto SaborHelado con los datos obtenidos.
                SaborHelado sabor = new SaborHelado(
                        nombre, sinTacc, vegano
                );

                // Convierte el texto de la base de datos al enum EstadoSabor.
                if (estadoStr != null) {
                    try {
                        sabor.setEstado(
                                EstadoSabor.valueOf(estadoStr.toUpperCase())
                        );
                    } catch (IllegalArgumentException e) {
                        // Se mantiene el estado por defecto si el valor no es válido.
                    }
                }

                // Crea una bacha con su capacidad máxima.
                Bacha<SaborHelado> bacha =
                        new Bacha<>(sabor, capacidadMaxKg);

                // Ajusta la cantidad de helado para representar el stock guardado.
                if (stockKg >= 0 && stockKg <= capacidadMaxKg) {

                    double diferencia = capacidadMaxKg - stockKg;

                    if (diferencia > 0) {
                        bacha.eliminarHelado(diferencia);
                    }
                }

                // Guarda la bacha en el mapa usando el nombre del sabor como clave.
                bachas.put(nombre.toLowerCase(), bacha);
            }

        } catch (SQLException | BachaVaciaException e) {
            System.err.println(
                    "ERROR: Fallo en ProductoDAO.obtenerTodasLasBachas - "
                            + e.getMessage()
            );
        }

        return bachas;
    }

    /**
     * Obtiene el stock de todos los tipos de postres.
     * Utiliza un HashMap donde la clave es el enum TipoPostre
     * y el valor es la cantidad disponible.
     */
    public Map<modelo.TipoPostre, Integer> obtenerStockPostres() {
        Map<modelo.TipoPostre, Integer> stockPostres = new HashMap<>();

        String sql = "SELECT tipo_postre, stock FROM stock_postres";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Convierte cada tipo de postre almacenado en su valor del enum.
            while (rs.next()) {
                modelo.TipoPostre tipo = modelo.TipoPostre.valueOf(
                        rs.getString("tipo_postre")
                );

                stockPostres.put(tipo, rs.getInt("stock"));
            }

        } catch (SQLException e) {
            System.err.println("ERROR al obtener stock de postres: " + e.getMessage());
        }

        return stockPostres;
    }

    /**
     * Descuenta unidades del stock de un tipo de postre.
     * La consulta verifica que exista stock suficiente antes de descontar.
     */
    public boolean descontarStockPostre(modelo.TipoPostre tipo, int cantidad) {
        String sql = "UPDATE stock_postres "
                + "SET stock = stock - ? "
                + "WHERE tipo_postre = ? AND stock >= ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, cantidad);
            stmt.setString(2, tipo.name());
            stmt.setInt(3, cantidad);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR al descontar stock de postre: " + e.getMessage());
            return false;
        }
    }

    /**
     * Incrementa el stock de un tipo de postre en la cantidad indicada.
     */
    public boolean reponerStockPostre(modelo.TipoPostre tipo, int cantidad) {
        String sql = "UPDATE stock_postres SET stock = stock + ? WHERE tipo_postre = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, cantidad);
            stmt.setString(2, tipo.name());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR al reponer stock de postre: " + e.getMessage());
            return false;
        }
    }

}