package br.ifsp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column(nullable = false)
    private String tipoVeiculo;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String cor;

    protected Veiculo() {}

    public Veiculo(String placa, String tipoVeiculo, String modelo, String cor) {

        if(placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");

        if(tipoVeiculo == null || tipoVeiculo.trim().isEmpty())
            throw new IllegalArgumentException("Tipo do veículo não pode ser nulo ou vazio");

        if (modelo == null || modelo.trim().isEmpty())
            throw new IllegalArgumentException("Modelo do veículo não pode ser nulo ou vazio");

        if (cor == null || cor.trim().isEmpty())
            throw new IllegalArgumentException("Cor do veículo não pode ser nula ou vazia");

        this.placa = placa;
        this.tipoVeiculo = tipoVeiculo;
        this.modelo = modelo;
        this.cor = cor;

    }

    public Long getId() {
        return id;
    }
    public String getPlaca() {
        return placa;
    }
    public String getTipoVeiculo() {
        return tipoVeiculo;
    }
    public String getModelo() {
        return modelo;
    }
    public String getCor() {
        return cor;
    }

}