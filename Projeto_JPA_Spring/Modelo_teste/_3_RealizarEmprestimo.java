package Projeto_JPA_Spring.Modelo_teste;

import Projeto_JPA_Spring.Modelo_basico.Emprestimo;
import Projeto_JPA_Spring.Modelo_basico.Livro;
import Projeto_JPA_Spring.Modelo_basico.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class _3_RealizarEmprestimo {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Usuario usuario = em.find(Usuario.class, 1L);
        Livro livro = em.find(Livro.class, 1L);

        if (usuario != null && livro != null) {
            if (livro.getQuantidadeDisponivel() > 0) {
                // Decrementa o estoque usando merge como no exemplo 4
                livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
                em.merge(livro);

                // Cria o empréstimo com prazo de 14 dias
                Emprestimo emprestimo = new Emprestimo(usuario, livro, 14);
                em.persist(emprestimo);

                em.getTransaction().commit();
                System.out.println("Empréstimo realizado para: " + usuario.getNome());
            } else {
                System.out.println("Livro indisponível no momento!");
                em.getTransaction().rollback();
            }
        }

        em.close();
        emf.close();
    }
}