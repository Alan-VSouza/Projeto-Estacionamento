package br.ifsp.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record VeiculoComVagaDTO(
        @NotBlank(message = "Placa não pode ser vazia")
        String placa,

        @NotBlank(message = "Tipo do veículo não pode ser vazio")
        String tipoVeiculo,

        @NotBlank(message = "Modelo não pode ser vazio")
        String modelo,

        @NotBlank(message = "Cor não pode ser vazia")
        String cor,

        @Min(value = 1, message = "ID da vaga deve ser maior que 0")
        @Max(value = 200, message = "ID da vaga deve ser menor ou igual a 200")
        Integer vagaId
) {}
