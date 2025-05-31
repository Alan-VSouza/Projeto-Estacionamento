package br.ifsp.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CriarEstacionamentoDTO {

    @NotBlank(message = "O nome não pode ser vazio ou nulo.")
    private String nome;

    @NotBlank(message = "O endereço não pode ser vazio ou nulo.")
    private String endereco;

    @Min(value = 1, message = "A capacidade deve ser de no mínimo 1.")
    private int capacidade;
}