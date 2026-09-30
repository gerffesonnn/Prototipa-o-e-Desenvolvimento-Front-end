package Projeto_JPA_Spring.Modelo_basico;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "emprestimo_id", nullable = false, unique = true)
    private Emprestimo emprestimo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private Boolean pago = false;

    private LocalDate dataGeracao;

    public Multa() {
    }

    public Multa(Emprestimo emprestimo, BigDecimal valor) {
        this.emprestimo = emprestimo;
        this.valor = valor;
        this.pago = false;
        this.dataGeracao = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Emprestimo getEmprestimo() { return emprestimo; }
    public void setEmprestimo(Emprestimo emprestimo) { this.emprestimo = emprestimo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public Boolean getPago() { return pago; }
    public void setPago(Boolean pago) { this.pago = pago; }
    public LocalDate getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDate dataGeracao) { this.dataGeracao = dataGeracao; }
}