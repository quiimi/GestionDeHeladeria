package dao;

import modelo.Cajero;
import modelo.Catering;
import modelo.Pedido;
import modelo.PedidoDelivery;

import java.sql.*;

public class VentaDAO {

    public boolean registrarPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos (cliente_dni, cajero_dni, fecha_hora, monto_total, tipo_pedido, numero_mesa, direccion) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pedido.getCliente() != null ? pedido.getCliente().getDni() : null);
            stmt.setString(2, pedido.getCajero().getDni());
            stmt.setTimestamp(3, Timestamp.valueOf(pedido.getFechaHora()));
            stmt.setDouble(4, pedido.calcularMontoTotal());
            stmt.setString(5, pedido.getTipoPedido());

            if (pedido instanceof Cajero.PedidoLocal) {
                stmt.setInt(6, ((Cajero.PedidoLocal) pedido).getNumeroMesa());
                stmt.setNull(7, Types.VARCHAR);
            } else if (pedido instanceof PedidoDelivery) {
                stmt.setNull(6, Types.INTEGER);
                stmt.setString(7, ((PedidoDelivery) pedido).getDireccionEntrega());
            }

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
            System.err.println("ERROR: Fallo en VentaDAO.registrarPedido - " + e.getMessage());
        }
        return false;
    }

    public boolean registrarCatering(Catering catering) {
        String sql = "INSERT INTO reservas_catering (cliente_dni, fecha_evento, cant_personas, monto_senia, monto_total, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, catering.getCliente().getDni());
            stmt.setTimestamp(2, Timestamp.valueOf(catering.getFechaEvento()));
            stmt.setInt(3, catering.getCantPersonas());
            stmt.setDouble(4, catering.getMontoSenia());
            stmt.setDouble(5, catering.getMontoTotal());
            stmt.setString(6, catering.getEstado());

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
            System.err.println("ERROR: Fallo en VentaDAO.registrarCatering - " + e.getMessage());
        }
        return false;
    }
}
