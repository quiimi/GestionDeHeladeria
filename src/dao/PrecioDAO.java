package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PrecioDAO {

    public Map<String, Double> obtenerTodosLosPrecios() {
        Map<String, Double> precios = new HashMap<>();
        String sql = "SELECT * FROM precios";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                precios.put(rs.getString("concepto"), rs.getDouble("precio"));
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PrecioDAO.obtenerTodosLosPrecios - " + e.getMessage());
        }
        return precios;
    }

    public boolean actualizarPrecio(String concepto, double nuevoPrecio) {
        String sql = "UPDATE precios SET precio = ? WHERE concepto = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoPrecio);
            stmt.setString(2, concepto);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PrecioDAO.actualizarPrecio - " + e.getMessage());
            return false;
        }
    }
}
