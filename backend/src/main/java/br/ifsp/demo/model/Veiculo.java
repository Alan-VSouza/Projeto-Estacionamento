package br.ifsp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import br.ifsp.demo.validation.ValidCor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Placa é obrigatória")
    private String placa;

    @Column(nullable = false)
    @NotBlank(message = "Tipo de veículo é obrigatório")
    private String tipoVeiculo;

    @Column(nullable = false)
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @Column(nullable = false)
    @NotBlank(message = "Cor é obrigatória")
    @ValidCor
    private String cor;

    private static final Pattern APENAS_NUMEROS = Pattern.compile("^\\d+$");
    private static final Pattern CARACTERES_ESPECIAIS = Pattern.compile("[^a-zA-ZÀ-ÿ\\s]");

    public Veiculo(String placa, String tipoVeiculo, String modelo, String cor) {

        if(placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");

        if(tipoVeiculo == null || tipoVeiculo.trim().isEmpty())
            throw new IllegalArgumentException("Tipo do veículo não pode ser nulo ou vazio");

        if (modelo == null || modelo.trim().isEmpty())
            throw new IllegalArgumentException("Modelo do veículo não pode ser nulo ou vazio");

        if (cor == null || cor.trim().isEmpty())
            throw new IllegalArgumentException("Cor do veículo não pode ser nula ou vazia");

        if (APENAS_NUMEROS.matcher(cor.trim()).matches()) {
            throw new IllegalArgumentException("Cor não pode ser apenas números. Use nomes como: branco, preto, azul, etc.");
        }

        if (CARACTERES_ESPECIAIS.matcher(cor.trim()).find()) {
            throw new IllegalArgumentException("Cor não pode conter caracteres especiais (@#!$%). Use apenas letras.");
        }

        this.placa = placa;
        this.tipoVeiculo = tipoVeiculo;
        this.modelo = modelo;
        this.cor = cor;
    }

    public Long getId() { return id; }
    public String getPlaca() { return placa; }
    public String getTipoVeiculo() { return tipoVeiculo; }
    public String getModelo() { return modelo; }
    public String getCor() { return cor; }
}
