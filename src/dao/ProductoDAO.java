package dao;

import modelo.Bacha;
import modelo.EstadoSabor;
import modelo.SaborHelado;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ProductoDAO {

    public boolean guardarSabor(SaborHelado sabor, double stockInicialKg) {
        String sql = "INSERT INTO sabores (nombre, sin_tacc, vegano, estado, stock_kg) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, sabor.getNombre());
            stmt.setBoolean(2, sabor.isSinTacc());
            stmt.setBoolean(3, sabor.isVegano());
            stmt.setString(4, sabor.getEstado().name());
            stmt.setDouble(5, stockInicialKg);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en ProductoDAO.guardarSabor - " + e.getMessage());
            return false;
        }
    }

    // Actualiza tanto el estado como la cantidad de kilos en la BD
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

    // Carga las bachas conservando el stock real guardado en la BD
    public Map<String, Bacha<SaborHelado>> obtenerTodasLasBachas() {
        Map<String, Bacha<SaborHelado>> bachas = new HashMap<>();
        String sql = "SELECT * FROM sabores";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                boolean sinTacc = rs.getBoolean("sin_tacc");
                boolean vegano = rs.getBoolean("vegano");
                String estadoStr = rs.getString("estado");
                double stockKg = rs.getDouble("stock_kg");

                SaborHelado sabor = new SaborHelado(nombre, sinTacc, vegano);
                if (estadoStr != null) {
                    try {
                        sabor.setEstado(EstadoSabor.valueOf(estadoStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        // Estado por defecto en caso de inconsistencia
                    }
                }

                Bacha<SaborHelado> bacha = new Bacha<>(sabor, 10.0);

                // Ajustar el stock actual de la bacha según lo leído de la BD
                double diferencia = 10.0 - stockKg;
                if (diferencia > 0 && diferencia <= 10.0) {
                    try {
                        bacha.eliminarHelado(diferencia);
                    } catch (Exception ignored) {
                    }
                }

                bachas.put(nombre.toLowerCase(), bacha);
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en ProductoDAO.obtenerTodasLasBachas - " + e.getMessage());
        }

        return bachas;
    }
}