package br.ifsp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDateTime horaEntrada;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column(nullable = false)
    private String tipoVeiculo;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String cor;

    public Veiculo() {}

    public Long getId() {
        return id;
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

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");
        this.placa = placa;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        if (tipoVeiculo == null || tipoVeiculo.trim().isEmpty())
            throw new IllegalArgumentException("Tipo do veículo não pode ser nulo ou vazio");
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty())
            throw new IllegalArgumentException("Modelo não pode ser nulo ou vazio");
        this.modelo = modelo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        if (cor == null || cor.trim().isEmpty())
            throw new IllegalArgumentException("Cor não pode ser nula ou vazia");
        this.cor = cor;
    }
}