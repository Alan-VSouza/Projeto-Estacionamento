package br.ifsp.demo.model;

import br.ifsp.demo.service.CalculadoraDeTarifa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class RegistroEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @Column(nullable = false)
    private LocalDateTime horaEntrada;

    @Column(nullable = false)
    private Integer vagaId;

    protected RegistroEntrada() {}

    public RegistroEntrada(Veiculo veiculo, Integer vagaId) {
        if(veiculo == null) {
            throw new IllegalArgumentException("Veículo não pode ser nulo");
        }
        if(vagaId == null || vagaId < 1 || vagaId > 200) {
            throw new IllegalArgumentException("ID da vaga deve estar entre 1 e 200");
        }

        this.veiculo = veiculo;
        this.horaEntrada = LocalDateTime.now();
        this.vagaId = vagaId;
    }

    public RegistroEntrada(Veiculo veiculo) {
        if(veiculo == null) {
            throw new IllegalArgumentException("Veículo não pode ser nulo");
        }
        this.veiculo = veiculo;
        this.horaEntrada = LocalDateTime.now();
        this.vagaId = 1;
    }

    public Pagamento finalizarEstadia(LocalDateTime horaSaida, CalculadoraDeTarifa calculadora) {
        return new Pagamento(this, horaSaida, calculadora);
    }

    public UUID getId() {
        return id;
    }
    public Veiculo getVeiculo() {
        return veiculo;
    }
    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public Integer getVagaId() {
        return vagaId;
    }
    public void setVagaId(Integer vagaId) {
        if(vagaId == null || vagaId < 1 || vagaId > 200) {
            throw new IllegalArgumentException("ID da vaga deve estar entre 1 e 200");
        }
        this.vagaId = vagaId;
    }
}
