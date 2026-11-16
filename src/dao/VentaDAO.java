package dao;

import modelo.Catering;
import modelo.Pedido;
import modelo.PedidoDelivery;
import modelo.PedidoLocal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de registrar pedidos y reservas de catering
 * y recuperar el historial de ventas desde la base de datos.
 */
public class VentaDAO {

    /**
     * Registra un pedido en la tabla pedidos.
     * Guarda los datos comunes y, según el tipo de pedido,
     * almacena el número de mesa o la dirección de entrega.
     *
     * También recupera el ID generado por la base de datos
     * y lo asigna al objeto Pedido.
     */
    public boolean registrarPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos "
                + "(cliente_dni, cajero_dni, fecha_hora, monto_total, tipo_pedido, numero_mesa, direccion, descripcion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Carga los datos comunes del pedido.
            stmt.setString(1, pedido.getCliente() != null ? pedido.getCliente().getDni() : null);
            stmt.setString(2, pedido.getCajero().getDni());
            stmt.setTimestamp(3, Timestamp.valueOf(pedido.getFechaHora()));
            stmt.setDouble(4, pedido.calcularMontoTotal());
            stmt.setString(5, pedido.getTipoPedido());

            // Determina qué datos específicos guardar según el tipo de pedido.
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

            // Guarda la descripción generada a partir de los productos.
            stmt.setString(8, pedido.generarDescripcion());

            int filas = stmt.executeUpdate();

            // Si se insertó el pedido, recupera su identificador generado.
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

    /**
     * Registra una reserva de catering en la base de datos.
     * Guarda los datos del cliente, cajero, fechas, personas e importes.
     * También recupera el identificador generado y lo asigna al objeto.
     */
    public boolean registrarCatering(Catering catering) {
        String sql = "INSERT INTO reservas_catering "
                + "(cliente_dni, cajero_dni, fecha_registro, fecha_evento, cant_personas, monto_senia, monto_total, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConeccionDB.obtenerConexion();
             PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asigna los datos de la reserva a los parámetros de la consulta.
            stmt.setString(1, catering.getCliente().getDni());
            stmt.setString(2, catering.getCajero().getDni());
            stmt.setTimestamp(3, Timestamp.valueOf(catering.getFechaRegistro()));
            stmt.setTimestamp(4, Timestamp.valueOf(catering.getFechaEvento()));
            stmt.setInt(5, catering.getCantPersonas());
            stmt.setDouble(6, catering.getMontoSenia());
            stmt.setDouble(7, catering.getMontoTotal());
            stmt.setString(8, catering.getEstado());

            int filas = stmt.executeUpdate();

            // Recupera el ID autogenerado si la reserva se insertó correctamente.
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

    /**
     * Obtiene el historial de ventas del sistema.
     * Combina los pedidos y las reservas de catering mediante UNION ALL,
     * ordenando todos los registros desde el más reciente al más antiguo.
     *
     * Devuelve una lista de textos con los datos de cada operación.
     */
    public List<String> obtenerHistorialVentas() {
        List<String> historial = new ArrayList<>();

        // Consulta que unifica los pedidos y las reservas de catering.
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

            // Recorre los resultados obtenidos de la consulta.
            while (rs.next()) {
                Timestamp fecha = rs.getTimestamp("fecha_hora");

                // Convierte la fecha de SQL a texto con formato día/mes/año y hora.
                String fechaTexto = fecha != null
                        ? fecha.toLocalDateTime().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "No disponible";

                // Construye una descripción completa de cada venta.
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