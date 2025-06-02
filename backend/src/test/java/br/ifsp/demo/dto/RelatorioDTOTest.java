package br.ifsp.demo.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class RelatorioDTOTest {

    @Test
    @Tag("UnitTest")
    @Tag("Mutation")
    @DisplayName("Deve criar um DTO de relatório com os valores corretos")
    public void deveCriarRelatorioComValoresCorretos() {
        int quantidade = 50;
        double tempoMedio = 3.5;
        double receitaTotal = 750.25;
        double ocupacaoMedia = 0.85;

        RelatorioDTO relatorio = RelatorioDTO.criarRelatorioDesempenho(
                quantidade,
                tempoMedio,
                receitaTotal,
                ocupacaoMedia
        );

        assertThat(relatorio).isNotNull();

        assertThat(relatorio.quantidade()).isEqualTo(quantidade);
        assertThat(relatorio.tempoMedioHoras()).isEqualTo(tempoMedio);
        assertThat(relatorio.receitaTotal()).isEqualTo(receitaTotal);
        assertThat(relatorio.ocupacaoMedia()).isEqualTo(ocupacaoMedia);
    }
}