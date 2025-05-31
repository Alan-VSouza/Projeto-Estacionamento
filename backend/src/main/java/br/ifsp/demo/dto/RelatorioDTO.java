package br.ifsp.demo.dto;

public record RelatorioDTO(
        int quantidade,
        double tempoMedioHoras,
        double receitaTotal,
        double ocupacaoMedia
) {
    public static RelatorioDTO criarRelatorioDesempenho(
            int quantidadeVeiculos,
            double tempoMedioHoras,
            double receitaTotal,
            double ocupacaoMedia) {
        return new RelatorioDTO(quantidadeVeiculos, tempoMedioHoras, receitaTotal, ocupacaoMedia);
    }
}
