package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/heladeria_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "1234";

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

        try {
            Class.forName(DRIVER);
            try (Connection con = DriverManager.getConnection(URL, USER, PASS);
                 Statement stmt = con.createStatement()) {

                // 1. Crear las tablas
                stmt.executeUpdate(sqlPersonas);
                stmt.executeUpdate(sqlSabores);
                stmt.executeUpdate(sqlPrecios);

                // 2. Insertar personas iniciales si la tabla está vacía
                ResultSet rsPersonas = stmt.executeQuery("SELECT COUNT(*) AS total FROM personas");
                if (rsPersonas.next() && rsPersonas.getInt("total") == 0) {
                    stmt.executeUpdate("INSERT INTO personas VALUES ('11111111', 'Carlos Duenio', 'DUENIO', 'carlos_admin', 0, 1)");
                    stmt.executeUpdate("INSERT INTO personas VALUES ('22222222', 'Laura Cajera', 'CAJERO', 'laura_caja', 0, 1)");
                    stmt.executeUpdate("INSERT INTO personas VALUES ('33333333', 'Pedro Cajero', 'CAJERO', 'pedro_caja', 0, 1)");
                    stmt.executeUpdate("INSERT INTO personas VALUES ('44444444', 'Juan Perez', 'CLIENTE', NULL, 100, 1)");
                    stmt.executeUpdate("INSERT INTO personas VALUES ('55555555', 'Maria Gomez', 'CLIENTE', NULL, 50, 1)");
                }

                // 3. Insertar sabores iniciales si la tabla está vacía
                ResultSet rsSabores = stmt.executeQuery("SELECT COUNT(*) AS total FROM sabores");
                if (rsSabores.next() && rsSabores.getInt("total") == 0) {
                    stmt.executeUpdate("INSERT INTO sabores VALUES ('dulce de leche', 1, 0, 'DISPONIBLE', 10.0)");
                    stmt.executeUpdate("INSERT INTO sabores VALUES ('chocolate amargo', 1, 1, 'DISPONIBLE', 10.0)");
                    stmt.executeUpdate("INSERT INTO sabores VALUES ('frutilla al agua', 1, 1, 'DISPONIBLE', 10.0)");
                    stmt.executeUpdate("INSERT INTO sabores VALUES ('tramontana', 0, 0, 'DISPONIBLE', 10.0)");
                    stmt.executeUpdate("INSERT INTO sabores VALUES ('vainilla', 1, 0, 'DISPONIBLE', 10.0)");
                }

                // 4. Insertar precios iniciales si la tabla está vacía
                ResultSet rsPrecios = stmt.executeQuery("SELECT COUNT(*) AS total FROM precios");
                if (rsPrecios.next() && rsPrecios.getInt("total") == 0) {
                    stmt.executeUpdate("INSERT INTO precios VALUES ('PRECIO_BOCHA', 1500.0)");
                    stmt.executeUpdate("INSERT INTO precios VALUES ('PRECIO_KILO', 12000.0)");
                    stmt.executeUpdate("INSERT INTO precios VALUES ('Catering', 5000.0)");
                    stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_ALMENDRADO', 8000.0)");
                    stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_CASSATA', 7500.0)");
                    stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_BOMBON_SUIZO', 8500.0)");
                    stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_TORTA_HELADA', 10000.0)");
                    stmt.executeUpdate("INSERT INTO precios VALUES ('POSTRE_ALFATOR', 6000.0)");
                }

                System.out.println("Base de datos, tablas y datos iniciales de prueba cargados correctamente.");

            }
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: No se encontro el Driver de MySQL - " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("ERROR al inicializar las tablas y datos: " + e.getMessage());
        }
    }
}