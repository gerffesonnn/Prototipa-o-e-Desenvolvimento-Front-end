package Projeto_JPA_Spring.Modelo_teste;

import Projeto_JPA_Spring.Modelo_basico.Emprestimo;
import Projeto_JPA_Spring.Modelo_basico.Multa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class _5_RelatoriosMovimentacao {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        EntityManager em = emf.createEntityManager();

        System.out.println("--- RELATÓRIO DE EMPRÉSTIMOS ---");
        String jpqlEmprestimos = "select e from Emprestimo e";
        TypedQuery<Emprestimo> queryE = em.createQuery(jpqlEmprestimos, Emprestimo.class);
        List<Emprestimo> emprestimos = queryE.getResultList();

        for (Emprestimo e : emprestimos) {
            System.out.println("ID: " + e.getId()
                    + " | Usuário: " + e.getUsuario().getNome()
                    + " | Livro: " + e.getLivro().getTitulo()
                    + " | Status: " + e.getStatus());
        }

        System.out.println("\n--- RELATÓRIO DE MULTAS ---");
        String jpqlMultas = "select m from Multa m";
        TypedQuery<Multa> queryM = em.createQuery(jpqlMultas, Multa.class);
        List<Multa> multas = queryM.getResultList();

        for (Multa m : multas) {
            System.out.println("ID Multa: " + m.getId()
                    + " | Usuário: " + m.getEmprestimo().getUsuario().getNome()
                    + " | Valor: R$ " + m.getValor()
                    + " | Pago: " + m.getPago());
        }

        em.close();
        emf.close();
    }
}