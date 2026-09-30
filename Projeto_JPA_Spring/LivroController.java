package Projeto_JPA_Spring;

import Projeto_JPA_Spring.Modelo_basico.Livro;
import Projeto_JPA_Spring.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @GetMapping
    public List<Livro> listar() {
        return livroRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Livro adicionar(@RequestBody LivroRequest request) {
        if (request.titulo() == null || request.titulo().isBlank()
                || request.autor() == null || request.autor().isBlank()
                || request.categoria() == null || request.categoria().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Título, autor e categoria são obrigatórios");
        }

        int quantidade = request.quantidadeTotal() == null ? 1 : request.quantidadeTotal();
        if (quantidade < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A quantidade deve ser maior que zero");
        }

        String isbn = request.isbn() == null || request.isbn().isBlank()
                ? UUID.randomUUID().toString()
                : request.isbn().trim();
        Livro livro = new Livro(request.titulo().trim(), request.autor().trim(),
                request.categoria().trim(), isbn, quantidade);
        return livroRepository.save(livro);
    }

    public record LivroRequest(String titulo, String autor, String categoria,
                               String isbn, Integer quantidadeTotal) {
    }
}
