package dao;

import modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de realizar las operaciones de persistencia
 * relacionadas con las personas: clientes, cajeros y dueño.
 */
public class PersonaDAO {

    /**
     * Guarda una persona en la tabla personas.
     * Determina su tipo mediante instanceof y almacena
     * los datos específicos según la clase a la que pertenece.
     */
    public boolean guardarPersona(Persona persona) {
        String sql = "INSERT INTO personas (dni, nombre, tipo, username, puntos_fidelidad, activo) "
                + "VALUES (?, ?, ?, ?, ?, 1)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, persona.getDni());
            stmt.setString(2, persona.getNombre());

            // Determina el tipo de persona según su clase.
            stmt.setString(3, (persona instanceof Duenio) ? "DUENIO" :
                    (persona instanceof Cajero) ? "CAJERO" : "CLIENTE");

            // Solo los usuarios tienen nombre de usuario.
            stmt.setString(4, (persona instanceof Usuario) ?
                    ((Usuario) persona).getUsername() : null);

            // Solo los clientes tienen puntos de fidelidad.
            stmt.setInt(5, (persona instanceof Cliente) ?
                    ((Cliente) persona).getPuntosFidelidad() : 0);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("ERROR en PersonaDAO.guardarPersona: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza los puntos de fidelidad de un cliente
     * utilizando su DNI para identificarlo.
     */
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

    /**
     * Obtiene todos los clientes activos de la base de datos.
     * Convierte cada registro obtenido en un objeto Cliente
     * y lo agrega a una lista.
     */
    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();

        // Consulta exclusiva para registros de tipo CLIENTE.
        String sql = "SELECT * FROM personas WHERE tipo = 'CLIENTE' AND activo = 1";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Recorre los resultados y reconstruye los objetos Cliente.
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

    /**
     * Busca una persona utilizando su DNI.
     * Según el tipo almacenado en la base de datos,
     * reconstruye y devuelve un Cliente, Cajero o Duenio.
     *
     * @return el objeto Persona encontrado o null si no existe.
     */
    public Persona buscarPorDni(String dni) {
        String sql = "SELECT * FROM personas WHERE dni = ?";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, dni);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("tipo");

                    // Si es cliente, también recupera sus puntos de fidelidad.
                    if ("CLIENTE".equalsIgnoreCase(tipo)) {
                        Cliente c = new Cliente(rs.getString("dni"), rs.getString("nombre"));
                        c.sumarPuntos(rs.getInt("puntos_fidelidad"));
                        return c;

                        // Si es cajero, recupera también su nombre de usuario.
                    } else if ("CAJERO".equalsIgnoreCase(tipo)) {
                        return new Cajero(rs.getString("dni"), rs.getString("nombre"), rs.getString("username"));

                        // Contempla las dos formas de escritura del tipo dueño.
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

    /**
     * Da de baja a un cajero modificando su campo activo a cero.
     * Solo afecta a cajeros que actualmente estén activos.
     */
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

    /**
     * Reactiva un cajero que se encontraba dado de baja.
     * Modifica su campo activo a uno.
     */
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

    /**
     * Consulta si una persona se encuentra activa en el sistema.
     *
     * @return true si está activa y false si no existe o está inactiva.
     */
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

    /**
     * Obtiene todos los cajeros activos y los convierte en objetos Cajero.
     */
    public List<Cajero> listarEmpleados() {
        List<Cajero> empleados = new ArrayList<>();

        // Consulta exclusiva para registros de tipo CAJERO.
        String sql = "SELECT * FROM personas WHERE tipo = 'CAJERO' AND activo = 1";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Recorre los resultados y crea un objeto por cada cajero.
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

    /**
     * Busca al dueño registrado en la base de datos.
     * Si encuentra un registro, crea y devuelve un objeto Duenio.
     */
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