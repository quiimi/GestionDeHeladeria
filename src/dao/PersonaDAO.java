package dao;
import modelo.Cajero;
import modelo.Cliente;
import modelo.Duenio;
import modelo.Persona;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public boolean guardarPersona(Persona persona) {
        String sql = "INSERT INTO personas (dni, nombre, tipo, username, puntos_fidelidad) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), username = VALUES(username), puntos_fidelidad = VALUES(puntos_fidelidad)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, persona.getDni());
            stmt.setString(2, persona.getNombre());

            if (persona instanceof Cliente) {
                stmt.setString(3, "CLIENTE");
                stmt.setNull(4, Types.VARCHAR);
                stmt.setInt(5, ((Cliente) persona).getPuntosFidelidad());
            } else if (persona instanceof Cajero) {
                stmt.setString(3, "CAJERO");
                stmt.setString(4, ((Cajero) persona).getUsername());
                stmt.setInt(5, 0);
            } else if (persona instanceof Duenio) {
                stmt.setString(3, "DUENO");
                stmt.setString(4, ((Duenio) persona).getUsername());
                stmt.setInt(5, 0);
            }

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PersonaDAO.guardarPersona - " + e.getMessage());
            return false;
        }
    }

    public Persona buscarPorDni(String dni) {
        String sql = "SELECT * FROM personas WHERE dni = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String tipo = rs.getString("tipo");

                    switch (tipo) {
                        case "CLIENTE":
                            Cliente cliente = new Cliente(dni, nombre);
                            cliente.sumarPuntos(rs.getInt("puntos_fidelidad"));
                            return cliente;
                        case "CAJERO":
                            return new Cajero(dni, nombre, rs.getString("username"));
                        case "DUENO":
                            return new Duenio(dni, nombre, rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en PersonaDAO.buscarPorDni - " + e.getMessage());
        }
        return null;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM personas WHERE tipo = 'CLIENTE'";

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
}