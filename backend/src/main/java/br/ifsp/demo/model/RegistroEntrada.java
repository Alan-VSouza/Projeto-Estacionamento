package br.ifsp.demo.model;

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
    @JoinColumn(name = "placa", referencedColumnName = "placa", nullable = false)
    private Veiculo veiculo;

    @Column(nullable = false)
    private LocalDateTime horaEntrada;

    public RegistroEntrada() {}

    public RegistroEntrada(Veiculo veiculo) {
        this.id = UUID.randomUUID();
        setVeiculo(veiculo);
        setHoraEntrada(LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não pode ser nulo");
        }
        this.veiculo = veiculo;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        if (horaEntrada == null)
            throw new IllegalArgumentException("Hora de entrada não pode ser nula");
        if (horaEntrada.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Hora de entrada não pode ser no futuro");

        this.horaEntrada = horaEntrada;
    }
}