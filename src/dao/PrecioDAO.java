package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO encargado de consultar y actualizar los precios
 * almacenados en la tabla precios de la base de datos.
 */
public class PrecioDAO {

    /**
     * Obtiene todos los precios registrados en la base de datos.
     * Los almacena en un HashMap donde la clave es el concepto
     * y el valor es el precio correspondiente.
     */
    public Map<String, Double> obtenerTodosLosPrecios() {
        Map<String, Double> precios = new HashMap<>();
        String sql = "SELECT * FROM precios";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Recorre los resultados y carga cada concepto con su precio.
            while (rs.next()) {
                precios.put(rs.getString("concepto"), rs.getDouble("precio"));
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PrecioDAO.obtenerTodosLosPrecios - " + e.getMessage());
        }
        return precios;
    }

    /**
     * Actualiza el precio de un concepto determinado.
     * Utiliza parámetros para indicar el nuevo precio y el concepto.
     *
     * @return true si se modificó algún registro, false si no hubo cambios.
     */
    public boolean actualizarPrecio(String concepto, double nuevoPrecio) {
        String sql = "UPDATE precios SET precio = ? WHERE concepto = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Asigna los valores a los parámetros de la consulta.
            stmt.setDouble(1, nuevoPrecio);
            stmt.setString(2, concepto);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PrecioDAO.actualizarPrecio - " + e.getMessage());
            return false;
        }
    }
}