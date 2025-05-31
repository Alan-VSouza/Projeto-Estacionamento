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

    protected RegistroEntrada() {}

    public RegistroEntrada(Veiculo veiculo) {

        if(veiculo == null)
            throw new IllegalArgumentException("Veículo não pode ser nulo");

        this.veiculo = veiculo;
        this.horaEntrada = LocalDateTime.now();
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

}