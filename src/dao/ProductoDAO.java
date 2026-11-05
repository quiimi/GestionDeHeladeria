package dao;

import modelo.EstadoSabor;
import modelo.SaborHelado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean guardarSabor(SaborHelado sabor) {
        String sql = "INSERT INTO sabores (nombre, es_sin_tacc, es_vegano, estado) VALUES (?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, sabor.getNombre());
            stmt.setBoolean(2, sabor.isEsSinTacc());
            stmt.setBoolean(3, sabor.isEsVegano());
            stmt.setString(4, sabor.getEstado().name());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en ProductoDAO.guardarSabor - " + e.getMessage());
            return false;
        }
    }

    public List<SaborHelado> obtenerTodosLosSabores() {
        List<SaborHelado> sabores = new ArrayList<>();
        String sql = "SELECT * FROM sabores";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SaborHelado s = new SaborHelado(
                        rs.getString("nombre"),
                        rs.getBoolean("es_sin_tacc"),
                        rs.getBoolean("es_vegano")
                );
                s.setEstado(EstadoSabor.valueOf(rs.getString("estado")));
                sabores.add(s);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en ProductoDAO.obtenerTodosLosSabores - " + e.getMessage());
        }
        return sabores;
    }

    public boolean actualizarEstadoSabor(String nombreSabor, EstadoSabor nuevoEstado) {
        String sql = "UPDATE sabores SET estado = ? WHERE nombre = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setString(2, nombreSabor);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en ProductoDAO.actualizarEstadoSabor - " + e.getMessage());
            return false;
        }
    }
}
