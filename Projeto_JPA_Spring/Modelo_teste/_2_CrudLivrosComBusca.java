package Projeto_JPA_Spring.Modelo_teste;

import Projeto_JPA_Spring.Modelo_basico.Livro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class _2_CrudLivrosComBusca {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        EntityManager em = emf.createEntityManager();

        String termo = "Java";

        // Busca com JPQL igual ao exemplo 3
        String jpql = "select l from Livro l where lower(l.titulo) like lower(:termo) or lower(l.autor) like lower(:termo)";
        TypedQuery<Livro> query = em.createQuery(jpql, Livro.class);
        query.setParameter("termo", "%" + termo + "%");

        List<Livro> livros = query.getResultList();

        for (Livro livro : livros) {
            System.out.println("ID: " + livro.getId()
                    + " | Título: " + livro.getTitulo()
                    + " | Autor: " + livro.getAutor()
                    + " | Disponíveis: " + livro.getQuantidadeDisponivel());
        }

        em.close();
        emf.close();
    }
}