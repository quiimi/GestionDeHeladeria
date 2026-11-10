package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void inicializarBaseDeDatos() {
        String sqlPersonas = "CREATE TABLE IF NOT EXISTS personas ("
                + "dni VARCHAR(20) PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "tipo VARCHAR(20) NOT NULL, "
                + "username VARCHAR(50), "
                + "puntos_fidelidad INT DEFAULT 0, "
                + "activo TINYINT(1) NOT NULL DEFAULT 1"
                + ");";

        String sqlSabores = "CREATE TABLE IF NOT EXISTS sabores ("
                + "nombre VARCHAR(100) PRIMARY KEY, "
                + "sin_tacc TINYINT(1) NOT NULL DEFAULT 0, "
                + "vegano TINYINT(1) NOT NULL DEFAULT 0, "
                + "estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE', "
                + "stock_kg DOUBLE NOT NULL DEFAULT 10.0"
                + ");";

        String sqlPrecios = "CREATE TABLE IF NOT EXISTS precios ("
                + "concepto VARCHAR(50) PRIMARY KEY, "
                + "precio DOUBLE NOT NULL"
                + ");";

        String sqlPedidos = "CREATE TABLE IF NOT EXISTS pedidos ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "cliente_dni VARCHAR(20), "
                + "cajero_dni VARCHAR(20) NOT NULL, "
                + "fecha_hora DATETIME NOT NULL, "
                + "monto_total DOUBLE NOT NULL, "
                + "tipo_pedido VARCHAR(20) NOT NULL, "
                + "numero_mesa INT, "
                + "direccion VARCHAR(150), "
                + "descripcion VARCHAR(500), "
                + "FOREIGN KEY (cliente_dni) REFERENCES personas(dni), "
                + "FOREIGN KEY (cajero_dni) REFERENCES personas(dni)"
                + ");";

        String sqlReservasCatering = "CREATE TABLE IF NOT EXISTS reservas_catering ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "cliente_dni VARCHAR(20) NOT NULL, "
                + "cajero_dni VARCHAR(20), "
                + "fecha_registro DATETIME, "
                + "fecha_evento DATETIME NOT NULL, "
                + "cant_personas INT NOT NULL, "
                + "monto_senia DOUBLE NOT NULL, "
                + "monto_total DOUBLE NOT NULL, "
                + "estado VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADA', "
                + "FOREIGN KEY (cliente_dni) REFERENCES personas(dni), "
                + "FOREIGN KEY (cajero_dni) REFERENCES personas(dni)"
                + ");";

        try (Connection con = ConeccionDB.obtenerConexion();
             Statement stmt = con.createStatement()) {

            // 1. Crear las tablas (personas primero: pedidos y reservas_catering la referencian por FK)
            stmt.executeUpdate(sqlPersonas);
            stmt.executeUpdate(sqlSabores);
            stmt.executeUpdate(sqlPrecios);
            stmt.executeUpdate(sqlPedidos);
            stmt.executeUpdate(sqlReservasCatering);

            // 2. Personas iniciales (el dueño NO se carga acá: se registra la primera vez desde Main)
            if (tablaVacia(stmt, "personas")) {
                stmt.executeUpdate("INSERT INTO personas VALUES ('22222222', 'Laura Cajera', 'CAJERO', 'laura_caja', 0, 1)");
                stmt.executeUpdate("INSERT INTO personas VALUES ('33333333', 'Pedro Cajero', 'CAJERO', 'pedro_caja', 0, 1)");
                stmt.executeUpdate("INSERT INTO personas VALUES ('44444444', 'Juan Perez', 'CLIENTE', NULL, 100, 1)");
                stmt.executeUpdate("INSERT INTO personas VALUES ('55555555', 'Maria Gomez', 'CLIENTE', NULL, 50, 1)");
            }

            // 3. Sabores iniciales
            if (tablaVacia(stmt, "sabores")) {
                stmt.executeUpdate("INSERT INTO sabores VALUES ('dulce de leche', 1, 0, 'DISPONIBLE', 10.0)");
                stmt.executeUpdate("INSERT INTO sabores VALUES ('chocolate amargo', 1, 1, 'DISPONIBLE', 10.0)");
                stmt.executeUpdate("INSERT INTO sabores VALUES ('frutilla al agua', 1, 1, 'DISPONIBLE', 10.0)");
                stmt.executeUpdate("INSERT INTO sabores VALUES ('tramontana', 0, 0, 'DISPONIBLE', 10.0)");
                stmt.executeUpdate("INSERT INTO sabores VALUES ('vainilla', 1, 0, 'DISPONIBLE', 10.0)");
            }

            // 4. Precios iniciales
            if (tablaVacia(stmt, "precios")) {
                stmt.executeUpdate("INSERT INTO precios VALUES ('PRECIO_BOCHA', 1500.0)");
                stmt.executeUpdate("INSERT INTO precios VALUES ('PRECIO_KILO', 12000.0)");
                stmt.executeUpdate("INSERT INTO precios VALUES ('Catering', 5000.0)");
                stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_ALMENDRADO', 8000.0)");
                stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_CASSATA', 7500.0)");
                stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_BOMBON_SUIZO', 8500.0)");
                stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_TORTA_HELADA', 10000.0)");
                stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_ALFATOR', 6000.0)");
            }

        } catch (SQLException e) {
            System.err.println("ERROR al inicializar las tablas y datos: " + e.getMessage());
        }
    }

    private static boolean tablaVacia(Statement stmt, String tabla) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM " + tabla)) {
            return rs.next() && rs.getInt("total") == 0;
        }
    }
}
