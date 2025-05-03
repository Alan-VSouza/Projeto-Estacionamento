package br.ifsp.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    private LocalDateTime horaEntrada;

    @Column(nullable = false)
    private LocalDateTime horaSaida;

    @Column(nullable = false)
    private double valor;

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