package br.ifsp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioDTO {
    private int quantidade;
    private double tempoMedioHoras;
    private double receitaTotal;
    private double ocupacaoMedia;

    public static RelatorioDTO criarRelatorioDesempenho(
            int quantidadeVeiculos,
            double tempoMedioHoras,
            double receitaTotal,
            double ocupacaoMedia) {
        return new RelatorioDTO(quantidadeVeiculos, tempoMedioHoras, receitaTotal, ocupacaoMedia);
    }
}
