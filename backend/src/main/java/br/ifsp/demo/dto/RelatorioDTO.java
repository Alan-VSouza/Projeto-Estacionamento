package br.ifsp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDTO {

    private int quantidadeVeiculos;
    private double tempoMedioEstadia;
    private double receitaTotal;
    private double ocupacaoMedia;
}