
package dao;

import modelo.Catering;
import modelo.Pedido;
import modelo.PedidoDelivery;
import modelo.PedidoLocal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    public boolean registrarPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos "
                + "(cliente_dni, cajero_dni, fecha_hora, monto_total, tipo_pedido, numero_mesa, direccion, descripcion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pedido.getCliente() != null ? pedido.getCliente().getDni() : null);
            stmt.setString(2, pedido.getCajero().getDni());
            stmt.setTimestamp(3, Timestamp.valueOf(pedido.getFechaHora()));
            stmt.setDouble(4, pedido.calcularMontoTotal());
            stmt.setString(5, pedido.getTipoPedido());

            if (pedido instanceof PedidoLocal) {
                stmt.setInt(6, ((PedidoLocal) pedido).getNumeroMesa());
                stmt.setNull(7, Types.VARCHAR);
            } else if (pedido instanceof PedidoDelivery) {
                stmt.setNull(6, Types.INTEGER);
                stmt.setString(7, ((PedidoDelivery) pedido).getDireccionEntrega());
            } else {
                stmt.setNull(6, Types.INTEGER);
                stmt.setNull(7, Types.VARCHAR);
            }

            stmt.setString(8, pedido.generarDescripcion());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedido.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en registrarPedido - " + e.getMessage());
        }

        return false;
    }

    public boolean registrarCatering(Catering catering) {
        String sql = "INSERT INTO reservas_catering "
                + "(cliente_dni, cajero_dni, fecha_registro, fecha_evento, cant_personas, monto_senia, monto_total, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, catering.getCliente().getDni());
            stmt.setString(2, catering.getCajero().getDni());
            stmt.setTimestamp(3, Timestamp.valueOf(catering.getFechaRegistro()));
            stmt.setTimestamp(4, Timestamp.valueOf(catering.getFechaEvento()));
            stmt.setInt(5, catering.getCantPersonas());
            stmt.setDouble(6, catering.getMontoSenia());
            stmt.setDouble(7, catering.getMontoTotal());
            stmt.setString(8, catering.getEstado());

            int filas = stmt.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        catering.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo en registrarCatering - " + e.getMessage());
        }

        return false;
    }

    public List<String> obtenerHistorialVentas() {
        List<String> historial = new ArrayList<>();

        String sql =
                "SELECT fecha_hora, tipo, descripcion, monto_total, cajero "
                        + "FROM ( "
                        + "   SELECT p.fecha_hora AS fecha_hora, "
                        + "          p.tipo_pedido AS tipo, "
                        + "          CONCAT(p.descripcion, "
                        + "                 CASE "
                        + "                   WHEN p.numero_mesa IS NOT NULL THEN CONCAT(' | Mesa: ', p.numero_mesa) "
                        + "                   WHEN p.direccion IS NOT NULL THEN CONCAT(' | Dirección: ', p.direccion) "
                        + "                   ELSE '' "
                        + "                 END) AS descripcion, "
                        + "          p.monto_total AS monto_total, "
                        + "          COALESCE(per.nombre, 'No disponible') AS cajero "
                        + "   FROM pedidos p "
                        + "   LEFT JOIN personas per ON p.cajero_dni = per.dni "
                        + "   UNION ALL "
                        + "   SELECT rc.fecha_registro AS fecha_hora, "
                        + "          'CATERING' AS tipo, "
                        + "          CONCAT('Reserva para ', rc.cant_personas, ' personas', "
                        + "                 ' | Cliente: ', COALESCE(cli.nombre, 'No disponible'), "
                        + "                 ' | Fecha del evento: ', DATE_FORMAT(rc.fecha_evento, '%d/%m/%Y %H:%i'), "
                        + "                 ' | Seña: $', rc.monto_senia, "
                        + "                 ' | Estado: ', rc.estado) AS descripcion, "
                        + "          rc.monto_total AS monto_total, "
                        + "          COALESCE(caj.nombre, 'No disponible') AS cajero "
                        + "   FROM reservas_catering rc "
                        + "   LEFT JOIN personas cli ON rc.cliente_dni = cli.dni "
                        + "   LEFT JOIN personas caj ON rc.cajero_dni = caj.dni "
                        + ") AS ventas "
                        + "ORDER BY fecha_hora DESC";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Timestamp fecha = rs.getTimestamp("fecha_hora");

                String fechaTexto = fecha != null
                        ? fecha.toLocalDateTime().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "No disponible";

                String venta = "Fecha: " + fechaTexto
                        + " | Tipo: " + rs.getString("tipo")
                        + " | Descripción: " + rs.getString("descripcion")
                        + " | Total: $" + rs.getDouble("monto_total")
                        + " | Cajero: " + rs.getString("cajero");

                historial.add(venta);
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo al obtener el historial de ventas - " + e.getMessage());
        }

        return historial;
    }
}
