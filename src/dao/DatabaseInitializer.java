package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de inicializar la base de datos.
 * Crea las tablas necesarias y carga los datos iniciales
 * cuando las tablas se encuentran vacías.
 */
public class DatabaseInitializer {

    /**
     * Crea las tablas del sistema y carga los registros iniciales.
     * Utiliza IF NOT EXISTS para evitar crear tablas que ya existen.
     */
    public static void inicializarBaseDeDatos() {

        // Define la estructura de la tabla de personas.
        String sqlPersonas = "CREATE TABLE IF NOT EXISTS personas ("
                + "dni VARCHAR(20) PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "tipo VARCHAR(20) NOT NULL, "
                + "username VARCHAR(50), "
                + "puntos_fidelidad INT DEFAULT 0, "
                + "activo TINYINT(1) NOT NULL DEFAULT 1"
                + ");";

        // Define la estructura de la tabla de sabores.
        String sqlSabores = "CREATE TABLE IF NOT EXISTS sabores ("
                + "nombre VARCHAR(100) PRIMARY KEY, "
                + "sin_tacc TINYINT(1) NOT NULL DEFAULT 0, "
                + "vegano TINYINT(1) NOT NULL DEFAULT 0, "
                + "estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE', "
                + "capacidad_max_kg DOUBLE NOT NULL DEFAULT 10.0, "
                + "stock_kg DOUBLE NOT NULL DEFAULT 10.0"
                + ");";

        // Define la tabla donde se almacenan los precios.
        String sqlPrecios = "CREATE TABLE IF NOT EXISTS precios ("
                + "concepto VARCHAR(50) PRIMARY KEY, "
                + "precio DOUBLE NOT NULL"
                + ");";

        // Define la tabla que almacena los pedidos realizados.
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

        // Define la tabla que almacena las reservas de catering.
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

        // Define la tabla que almacena el stock de los postres.
        String sqlStockPostres = "CREATE TABLE IF NOT EXISTS stock_postres ("
                + "tipo_postre VARCHAR(50) PRIMARY KEY, "
                + "stock INT NOT NULL DEFAULT 10"
                + ");";

        try (Connection con = ConeccionDB.obtenerConexion();
             Statement stmt = con.createStatement()) {

            // 1. Crea las tablas respetando el orden de las claves foráneas.
            stmt.executeUpdate(sqlPersonas);
            stmt.executeUpdate(sqlSabores);

            // Verifica si la columna capacidad_max_kg ya existe.
            // Si no existe, la agrega para actualizar una estructura anterior.
            try (ResultSet rs = con.getMetaData().getColumns(
                    null, null, "sabores", "capacidad_max_kg")) {

                if (!rs.next()) {
                    stmt.executeUpdate(
                            "ALTER TABLE sabores ADD COLUMN capacidad_max_kg "
                                    + "DOUBLE NOT NULL DEFAULT 10.0 AFTER estado"
                    );
                }
            }

            stmt.executeUpdate(sqlPrecios);
            stmt.executeUpdate(sqlPedidos);
            stmt.executeUpdate(sqlReservasCatering);
            stmt.executeUpdate(sqlStockPostres);

            // 2. Carga personas de ejemplo solamente si la tabla está vacía.
            // El dueño se registra desde el menú principal.
            if (tablaVacia(stmt, "personas")) {
                stmt.executeUpdate("INSERT INTO personas VALUES ('22222222', 'Laura Cajera', 'CAJERO', 'laura_caja', 0, 1)");
                stmt.executeUpdate("INSERT INTO personas VALUES ('33333333', 'Pedro Cajero', 'CAJERO', 'pedro_caja', 0, 1)");
                stmt.executeUpdate("INSERT INTO personas VALUES ('44444444', 'Juan Perez', 'CLIENTE', NULL, 100, 1)");
                stmt.executeUpdate("INSERT INTO personas VALUES ('55555555', 'Maria Gomez', 'CLIENTE', NULL, 50, 1)");
            }

            // 3. Carga los sabores iniciales si todavía no existen registros.
            if (tablaVacia(stmt, "sabores")) {
                stmt.executeUpdate(
                        "INSERT INTO sabores "
                                + "(nombre, sin_tacc, vegano, estado, capacidad_max_kg, stock_kg) "
                                + "VALUES ('dulce de leche', 1, 0, 'DISPONIBLE', 10.0, 10.0)"
                );
                stmt.executeUpdate(
                        "INSERT INTO sabores "
                                + "(nombre, sin_tacc, vegano, estado, capacidad_max_kg, stock_kg) "
                                + "VALUES ('chocolate amargo', 1, 1, 'DISPONIBLE', 10.0, 10.0)"
                );
                stmt.executeUpdate(
                        "INSERT INTO sabores "
                                + "(nombre, sin_tacc, vegano, estado, capacidad_max_kg, stock_kg) "
                                + "VALUES ('frutilla al agua', 1, 1, 'DISPONIBLE', 10.0, 10.0)"
                );
                stmt.executeUpdate(
                        "INSERT INTO sabores "
                                + "(nombre, sin_tacc, vegano, estado, capacidad_max_kg, stock_kg) "
                                + "VALUES ('tramontana', 0, 0, 'DISPONIBLE', 10.0, 10.0)"
                );
                stmt.executeUpdate(
                        "INSERT INTO sabores "
                                + "(nombre, sin_tacc, vegano, estado, capacidad_max_kg, stock_kg) "
                                + "VALUES ('vainilla', 1, 0, 'DISPONIBLE', 10.0, 10.0)"
                );
            }

            // 4. Carga los precios iniciales si la tabla está vacía.
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

            // Carga el stock inicial de cada tipo de postre.
            // Recorre los valores definidos en el enum TipoPostre.
            if (tablaVacia(stmt, "stock_postres")) {
                for (modelo.TipoPostre tipo : modelo.TipoPostre.values()) {
                    stmt.executeUpdate(
                            "INSERT INTO stock_postres (tipo_postre, stock) VALUES ('"
                                    + tipo.name() + "', 10)"
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR al inicializar las tablas y datos: " + e.getMessage());
        }
    }

    /**
     * Verifica si una tabla no contiene registros.
     * Ejecuta una consulta COUNT para contar sus filas.
     *
     * @return true si la tabla está vacía, false en caso contrario.
     */
    private static boolean tablaVacia(Statement stmt, String tabla) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM " + tabla)) {
            return rs.next() && rs.getInt("total") == 0;
        }
    }
}