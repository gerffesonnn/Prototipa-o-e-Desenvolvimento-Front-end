package Projeto_JPA_Spring.Modelo_basico;


import Projeto_JPA_Spring.Modelo_teste._6_ConexaoBanco;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BibliotecaService {

    // --- MÓDULO: CADASTRO DE USUÁRIOS ---
    public void cadastrarUsuario(String nome, String email, String telefone) {
        String sql = "INSERT INTO usuario (nome, email, telefone) VALUES (?, ?, ?)";
        try (Connection conn = _6_ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, telefone);
            stmt.executeUpdate();
            System.out.println("\nUsuário '" + nome + "' cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public void listarUsuarios() {
        String sql = "SELECT * FROM usuario";
        try (Connection conn = _6_ConexaoBanco.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- LISTA DE USUÁRIOS ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Nome: %s | Email: %s | Multas Pendentes: R$ %.2f%n",
                        rs.getInt("id"), rs.getString("nome"), rs.getString("email"), rs.getDouble("total_multa"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
    }

    // --- MÓDULO: CRUD DE LIVROS ---
    public void cadastrarLivro(String titulo, String autor, String categoria) {
        String sql = "INSERT INTO livro (titulo, autor, categoria, disponivel) VALUES (?, ?, ?, TRUE)";
        try (Connection conn = _6_ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, titulo);
            stmt.setString(2, autor);
            stmt.setString(3, categoria);
            stmt.executeUpdate();
            System.out.println("\nLivro '" + titulo + "' adicionado ao acervo!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar livro: " + e.getMessage());
        }
    }

    public void listarLivros(String busca) {
        String sql = "SELECT * FROM livro WHERE LOWER(titulo) LIKE ? OR LOWER(autor) LIKE ?";
        try (Connection conn = _6_ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String termo = "%" + (busca == null ? "" : busca.toLowerCase()) + "%";
            stmt.setString(1, termo);
            stmt.setString(2, termo);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- ACERVO DE LIVROS ---");
            while (rs.next()) {
                String status = rs.getBoolean("disponivel") ? "[Disponível]" : "[Emprestado]";
                System.out.printf("Código: %d | %s - %s (%s) %s%n",
                        rs.getInt("codigo"), rs.getString("titulo"), rs.getString("autor"),
                        rs.getString("categoria"), status);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar livros: " + e.getMessage());
        }
    }

    // --- MÓDULO: EMPRÉSTIMO COM DATAS LIMITE ---
    public void realizarEmprestimo(int idUsuario, int codigoLivro, int diasPrazo) {
        String sqlCheckLivro = "SELECT disponivel, titulo FROM livro WHERE codigo = ?";
        String sqlCheckUser = "SELECT id FROM usuario WHERE id = ?";
        String sqlInsert = "INSERT INTO emprestimo (id_usuario, codigo_livro, data_emprestimo, data_prevista, status) VALUES (?, ?, ?, ?, 'EMPRESTADO')";
        String sqlUpdateLivro = "UPDATE livro SET disponivel = FALSE WHERE codigo = ?";

        try (Connection conn = _6_ConexaoBanco.conectar()) {
            if (conn == null) return;
            conn.setAutoCommit(false);

            // Verifica Usuário
            try (PreparedStatement stmtU = conn.prepareStatement(sqlCheckUser)) {
                stmtU.setInt(1, idUsuario);
                if (!stmtU.executeQuery().next()) {
                    System.out.println("\nUsuário não encontrado.");
                    return;
                }
            }

            // Verifica Disponibilidade do Livro
            try (PreparedStatement stmtL = conn.prepareStatement(sqlCheckLivro)) {
                stmtL.setInt(1, codigoLivro);
                ResultSet rsL = stmtL.executeQuery();
                if (!rsL.next() || !rsL.getBoolean("disponivel")) {
                    System.out.println("\nLivro indisponível ou não cadastrado.");
                    return;
                }
            }

            LocalDate hoje = LocalDate.now();
            LocalDate dataPrevista = hoje.plusDays(diasPrazo);

            // Registra Empréstimo
            try (PreparedStatement stmtI = conn.prepareStatement(sqlInsert)) {
                stmtI.setInt(1, idUsuario);
                stmtI.setInt(2, codigoLivro);
                stmtI.setDate(3, Date.valueOf(hoje));
                stmtI.setDate(4, Date.valueOf(dataPrevista));
                stmtI.executeUpdate();
            }

            // Atualiza Livro para Indisponível
            try (PreparedStatement stmtU = conn.prepareStatement(sqlUpdateLivro)) {
                stmtU.setInt(1, codigoLivro);
                stmtU.executeUpdate();
            }

            conn.commit();
            System.out.println("\nEmpréstimo realizado com sucesso! Data limite de devolução: " + dataPrevista);

        } catch (SQLException e) {
            System.out.println("Erro na transação de empréstimo: " + e.getMessage());
        }
    }

    // --- MÓDULO: DEVOLUÇÃO E MULTA ---
    public void realizarDevolucao(int idEmprestimo, double valorMultaPorDia) {
        String sqlEmp = "SELECT * FROM emprestimo WHERE id = ? AND status = 'EMPRESTADO'";
        String sqlUpdateEmp = "UPDATE emprestimo SET data_devolucao = ?, valor_multa = ?, status = 'DEVOLVIDO' WHERE id = ?";
        String sqlUpdateLivro = "UPDATE livro SET disponivel = TRUE WHERE codigo = ?";
        String sqlUpdateUser = "UPDATE usuario SET total_multa = total_multa + ? WHERE id = ?";

        try (Connection conn = _6_ConexaoBanco.conectar()) {
            if (conn == null) return;
            conn.setAutoCommit(false);

            int codigoLivro = 0;
            int idUsuario = 0;
            LocalDate dataPrevista = null;

            try (PreparedStatement stmt = conn.prepareStatement(sqlEmp)) {
                stmt.setInt(1, idEmprestimo);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("\nEmpréstimo ativo não encontrado.");
                    return;
                }
                codigoLivro = rs.getInt("codigo_livro");
                idUsuario = rs.getInt("id_usuario");
                dataPrevista = rs.getDate("data_prevista").toLocalDate();
            }

            LocalDate hoje = LocalDate.now();
            long diasAtraso = ChronoUnit.DAYS.between(dataPrevista, hoje);
            double multaCalculada = 0.0;

            if (diasAtraso > 0) {
                multaCalculada = diasAtraso * valorMultaPorDia;
                System.out.printf("%nDevolução em atraso (%d dias). Multa aplicada: R$ %.2f%n", diasAtraso, multaCalculada);
            } else {
                System.out.println("\nDevolução realizada dentro do prazo. Sem multas.");
            }

            // Atualiza Registro de Empréstimo
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateEmp)) {
                stmt.setDate(1, Date.valueOf(hoje));
                stmt.setDouble(2, multaCalculada);
                stmt.setInt(3, idEmprestimo);
                stmt.executeUpdate();
            }

            // Libera Livro
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateLivro)) {
                stmt.setInt(1, codigoLivro);
                stmt.executeUpdate();
            }

            // Aplica Multa ao Usuário se houver
            if (multaCalculada > 0) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateUser)) {
                    stmt.setDouble(1, multaCalculada);
                    stmt.setInt(2, idUsuario);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("Devolução concluída com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro no processo de devolução: " + e.getMessage());
        }
    }

    // --- MÓDULO: RELATÓRIOS E HISTÓRICO ---
    public void exibirHistorico() {
        String sql = "SELECT e.id, u.nome AS usuario, l.titulo AS livro, e.data_emprestimo, " +
                "e.data_prevista, e.data_devolucao, e.valor_multa, e.status " +
                "FROM emprestimo e " +
                "JOIN usuario u ON e.id_usuario = u.id " +
                "JOIN livro l ON e.codigo_livro = l.codigo ORDER BY e.id DESC";

        try (Connection conn = _6_ConexaoBanco.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n---------------- RELATÓRIO DE MOVIMENTAÇÃO ----------------");
            while (rs.next()) {
                System.out.printf("ID Empréstimo: %d | Usuário: %s | Livro: %s | Início: %s | Previsto: %s | Status: %s | Multa: R$ %.2f%n",
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("livro"),
                        rs.getDate("data_emprestimo"),
                        rs.getDate("data_prevista"),
                        rs.getString("status"),
                        rs.getDouble("valor_multa"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }
}

