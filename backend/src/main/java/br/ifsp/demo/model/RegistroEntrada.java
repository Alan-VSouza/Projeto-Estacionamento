package br.ifsp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class RegistroEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "placa", referencedColumnName = "placa", nullable = false)
    private Veiculo veiculo;

    @Column(nullable = false)
    private LocalDateTime horaEntrada;

    public RegistroEntrada() {}

    public RegistroEntrada(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.horaEntrada = LocalDateTime.now();
    }
}
