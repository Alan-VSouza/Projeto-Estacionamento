package br.ifsp.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

public record CriarEstacionamentoDTO(
        @NotBlank(message = "Nome não pode ser vazio")
        String nome,

        @NotBlank(message = "Endereço não pode ser vazio")
        String endereco,

        @Min(value = 1, message = "Capacidade deve ser maior que 0")
        int capacidade
) {}
