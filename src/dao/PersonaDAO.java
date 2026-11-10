package dao;

import modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public boolean guardarPersona(Persona persona) {
        String sql = "INSERT INTO personas (dni, nombre, tipo, username, puntos_fidelidad, activo) "
                + "VALUES (?, ?, ?, ?, ?, 1)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, persona.getDni());
            stmt.setString(2, persona.getNombre());
            stmt.setString(3, (persona instanceof Duenio) ? "DUENIO" :
                    (persona instanceof Cajero) ? "CAJERO" : "CLIENTE");
            stmt.setString(4, (persona instanceof Usuario) ?
                    ((Usuario) persona).getUsername() : null);
            stmt.setInt(5, (persona instanceof Cliente) ?
                    ((Cliente) persona).getPuntosFidelidad() : 0);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR en PersonaDAO.guardarPersona: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPuntosCliente(Cliente cliente) {
        String sql = "UPDATE personas SET puntos_fidelidad = ? WHERE dni = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, cliente.getPuntosFidelidad());
            stmt.setString(2, cliente.getDni());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PersonaDAO.actualizarPuntosCliente - " + e.getMessage());
            return false;
        }
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        // Consulta exclusiva para registros de tipo CLIENTE
        String sql = "SELECT * FROM personas WHERE tipo = 'CLIENTE' AND activo = 1";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente(rs.getString("dni"), rs.getString("nombre"));
                c.sumarPuntos(rs.getInt("puntos_fidelidad"));
                clientes.add(c);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PersonaDAO.listarClientes - " + e.getMessage());
        }
        return clientes;
    }

    public Persona buscarPorDni(String dni) {
        String sql = "SELECT * FROM personas WHERE dni = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    if ("CLIENTE".equalsIgnoreCase(tipo)) {
                        Cliente c = new Cliente(rs.getString("dni"), rs.getString("nombre"));
                        c.sumarPuntos(rs.getInt("puntos_fidelidad"));
                        return c;
                    } else if ("CAJERO".equalsIgnoreCase(tipo)) {
                        return new Cajero(rs.getString("dni"), rs.getString("nombre"), rs.getString("username"));
                    } else if ("DUENIO".equalsIgnoreCase(tipo) || "DUENO".equalsIgnoreCase(tipo)) {
                        return new Duenio(rs.getString("dni"), rs.getString("nombre"), rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PersonaDAO.buscarPorDni - " + e.getMessage());
        }
        return null;
    }

    public boolean darDeBajaCajero(String dni) {
        String sql = "UPDATE personas SET activo = 0 WHERE dni = ? AND tipo = 'CAJERO' AND activo = 1";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, dni);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR en PersonaDAO.darDeBajaCajero: " + e.getMessage());
            return false;
        }
    }

    public boolean reactivarCajero(String dni) {
        String sql = "UPDATE personas SET activo = 1 "
                + "WHERE dni = ? AND tipo = 'CAJERO' AND activo = 0";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, dni);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR en PersonaDAO.reactivarCajero: " + e.getMessage());
            return false;
        }
    }

    public boolean estaActivo(String dni) {
        String sql = "SELECT activo FROM personas WHERE dni = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("activo") == 1;
            }

        } catch (SQLException e) {
            System.err.println("ERROR en PersonaDAO.estaActivo: " + e.getMessage());
            return false;
        }
    }

    public List<Cajero> listarEmpleados() {
        List<Cajero> empleados = new ArrayList<>();
        // Consulta exclusiva para registros de tipo CAJERO
        String sql = "SELECT * FROM personas WHERE tipo = 'CAJERO' AND activo = 1";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cajero c = new Cajero(
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("username")
                );
                empleados.add(c);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PersonaDAO.listarEmpleados - " + e.getMessage());
        }
        return empleados;
    }

    public Duenio buscarDuenio() {
        String sql = "SELECT * FROM personas WHERE tipo IN ('DUENIO', 'DUENO') ORDER BY dni LIMIT 1";
        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new Duenio(rs.getString("dni"), rs.getString("nombre"), rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PersonaDAO.buscarDuenio - " + e.getMessage());
        }
        return null;
    }

}
