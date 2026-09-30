package Projeto_JPA_Spring.Modelo_teste;

import Projeto_JPA_Spring.Modelo_basico.Usuario;
import Projeto_JPA_Spring.Modelo_basico.Livro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class _1_CadastrarDados {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("biblioteca");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Usuario usuario1 = new Usuario("Carlos Silva", "carlos@gmail.com", "11988887777");
        Usuario usuario2 = new Usuario("Ana Souza", "ana@gmail.com", "11977776666");

        Livro livro1 = new Livro("Java para Iniciantes", "Herbert Schildt", "978-85-8055-000-1", 5);
        Livro livro2 = new Livro("Clean Code", "Robert C. Martin", "978-85-7608-267-5", 2);

        em.persist(usuario1);
        em.persist(usuario2);
        em.persist(livro1);
        em.persist(livro2);

        em.getTransaction().commit();

        System.out.println("Usuários e Livros cadastrados com sucesso!");

        em.close();
        emf.close();
    }
}