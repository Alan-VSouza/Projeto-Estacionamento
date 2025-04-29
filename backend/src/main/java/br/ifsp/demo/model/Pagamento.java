package br.ifsp.demo.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "placa", referencedColumnName = "placa", nullable = false)
    private Veiculo veiculo;

    private LocalDateTime horaEntrada;

    private LocalDateTime horaSaida;

    private double valor;

    public Pagamento() {}

    public Pagamento(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.horaEntrada = veiculo.getEntrada();
        this.horaSaida = LocalDateTime.now();
        this.valor = 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(uuid, pagamento.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}