package dao;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void inicializarBaseDeDatos() {
        String sqlPersonas = "CREATE TABLE IF NOT EXISTS personas ("
                + "dni VARCHAR(20) PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "tipo VARCHAR(20) NOT NULL, "
                + "username VARCHAR(50), "
                + "puntos_fidelidad INT DEFAULT 0"
                + ");";

        String sqlSabores = "CREATE TABLE IF NOT EXISTS sabores ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "es_sin_tacc BOOLEAN DEFAULT FALSE, "
                + "es_vegano BOOLEAN DEFAULT FALSE, "
                + "estado VARCHAR(30) DEFAULT 'DISPONIBLE'"
                + ");";

        String sqlCatering = "CREATE TABLE IF NOT EXISTS reservas_catering ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "cliente_dni VARCHAR(20) NOT NULL, "
                + "fecha_evento DATETIME NOT NULL, "
                + "cant_personas INT NOT NULL, "
                + "monto_senia DOUBLE NOT NULL, "
                + "monto_total DOUBLE NOT NULL, "
                + "estado VARCHAR(20) DEFAULT 'CONFIRMADA', "
                + "FOREIGN KEY (cliente_dni) REFERENCES personas(dni)"
                + ");";

        String sqlPedidos = "CREATE TABLE IF NOT EXISTS pedidos ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "cliente_dni VARCHAR(20), "
                + "cajero_dni VARCHAR(20) NOT NULL, "
                + "fecha_hora DATETIME NOT NULL, "
                + "monto_total DOUBLE NOT NULL, "
                + "tipo_pedido VARCHAR(20) NOT NULL, "
                + "numero_mesa INT, "
                + "direccion VARCHAR(200), "
                + "FOREIGN KEY (cliente_dni) REFERENCES personas(dni), "
                + "FOREIGN KEY (cajero_dni) REFERENCES personas(dni)"
                + ");";

        try (Connection con = ConeccionDB.obtenerConexion();
             Statement stmt = con.createStatement()) {

            if (con != null) {
                stmt.executeUpdate(sqlPersonas);
                stmt.executeUpdate(sqlSabores);
                stmt.executeUpdate(sqlCatering);
                stmt.executeUpdate(sqlPedidos);
            }

        } catch (SQLException e) {
            System.err.println("ERROR: Fallo al inicializar las tablas - " + e.getMessage());
        }
    }
}