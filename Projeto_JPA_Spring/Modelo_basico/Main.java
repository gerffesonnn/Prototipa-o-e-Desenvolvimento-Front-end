package Projeto_JPA_Spring.Modelo_basico;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BibliotecaService servico = new BibliotecaService();
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n========================================");
            System.out.println("     SISTEMA DE GESTÃO DE BIBLIOTECA    ");
            System.out.println("========================================");
            System.out.println("1. Cadastrar Usuário");
            System.out.println("2. Listar Usuários");
            System.out.println("3. Cadastrar Livro");
            System.out.println("4. Buscar Livros no Acervo");
            System.out.println("5. Realizar Empréstimo (com Prazo)");
            System.out.println("6. Realizar Devolução (com Cálculo de Multa)");
            System.out.println("7. Relatório de Movimentação e Histórico");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Nome do usuário: ");
                    String nome = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Telefone: ");
                    String tel = scanner.nextLine();
                    servico.cadastrarUsuario(nome, email, tel);
                    break;
                case 2:
                    servico.listarUsuarios();
                    break;
                case 3:
                    System.out.print("Título do livro: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Autor: ");
                    String autor = scanner.nextLine();
                    System.out.print("Categoria: ");
                    String cat = scanner.nextLine();
                    servico.cadastrarLivro(titulo, autor, cat);
                    break;
                case 4:
                    System.out.print("Termo para busca (Enter para todos): ");
                    String busca = scanner.nextLine();
                    servico.listarLivros(busca);
                    break;
                case 5:
                    System.out.print("ID do Usuário: ");
                    int idUser = scanner.nextInt();
                    System.out.print("Código do Livro: ");
                    int codLivro = scanner.nextInt();
                    System.out.print("Prazo de devolução (em dias): ");
                    int dias = scanner.nextInt();
                    servico.realizarEmprestimo(idUser, codLivro, dias);
                    break;
                case 6:
                    System.out.print("ID do Empréstimo: ");
                    int idEmp = scanner.nextInt();
                    System.out.print("Taxa de multa por dia de atraso (R$): ");
                    double taxa = scanner.nextDouble();
                    servico.realizarDevolucao(idEmp, taxa);
                    break;
                case 7:
                    servico.exibirHistorico();
                    break;
                case 0:
                    System.out.println("Encerrando o sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        scanner.close();
    }
}
