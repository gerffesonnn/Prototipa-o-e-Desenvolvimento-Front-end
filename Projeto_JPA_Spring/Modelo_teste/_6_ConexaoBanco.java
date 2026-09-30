package Projeto_JPA_Spring.Modelo_teste;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class _6_ConexaoBanco {
    private static final String URL = "jdbc:mysql://localhost:3306/db_biblioteca";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Erro de conexão com o banco de dados: " + e.getMessage());
            return null;
        }
    }
}

