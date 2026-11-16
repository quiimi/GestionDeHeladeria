package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de establecer la conexión entre el sistema
 * y la base de datos MySQL.
 */
public class ConeccionDB {

    // Datos necesarios para conectarse a la base de datos.
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/heladeria_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "1234";

    /**
     * Carga el driver de MySQL y devuelve una conexión
     * con la base de datos.
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: No se encontro el Driver de MySQL - " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}