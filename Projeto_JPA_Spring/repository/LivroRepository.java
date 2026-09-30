package Projeto_JPA_Spring.repository;

import Projeto_JPA_Spring.Modelo_basico.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}