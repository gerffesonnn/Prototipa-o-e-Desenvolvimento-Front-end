package Projeto_JPA_Spring.Modelo_teste;

import Projeto_JPA_Spring.Modelo_basico.Emprestimo;
import Projeto_JPA_Spring.Modelo_basico.Livro;
import Projeto_JPA_Spring.Modelo_basico.Multa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class _4_RealizarDevolucao {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Emprestimo emprestimo = em.find(Emprestimo.class, 1L);

        if (emprestimo != null && "ATIVO".equals(emprestimo.getStatus())) {
            LocalDate hoje = LocalDate.now();
            emprestimo.setDataDevolucao(hoje);
            emprestimo.setStatus("CONCLUIDO");

            // Devolve cópia ao estoque
            Livro livro = emprestimo.getLivro();
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
            em.merge(livro);

            // Verifica se devolveu com atraso
            if (hoje.isAfter(emprestimo.getDataPrevistaDevolucao())) {
                long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataPrevistaDevolucao(), hoje);
                BigDecimal valorMulta = new BigDecimal("2.00").multiply(new BigDecimal(diasAtraso));

                Multa multa = new Multa(emprestimo, valorMulta);
                em.persist(multa);

                System.out.println("Devolução com atraso! Multa gerada: R$ " + valorMulta);
            } else {
                System.out.println("Devolução dentro do prazo realizada com sucesso!");
            }

            em.merge(emprestimo);
            em.getTransaction().commit();
        }

        em.close();
        emf.close();
    }
}
