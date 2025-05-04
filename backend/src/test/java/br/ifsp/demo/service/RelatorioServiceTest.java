package br.ifsp.demo.service;

import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private final LocalDate dataReferencia = LocalDate.of(2025, 5, 3);

    private List<Pagamento> pagamentosDeTeste;

    @BeforeEach
    void setup() {
        Pagamento p1 = new Pagamento();
        p1.setPlaca("ABC1234");
        p1.setHoraEntrada(LocalDateTime.of(2025, 5, 3, 10, 0));
        p1.setHoraSaida(LocalDateTime.of(2025, 5, 3, 12, 0));
        p1.setValor(35.0);

        Pagamento p2 = new Pagamento();
        p2.setPlaca("XYZ5678");
        p2.setHoraEntrada(LocalDateTime.of(2025, 5, 3, 14, 0));
        p2.setHoraSaida(LocalDateTime.of(2025, 5, 3, 16, 30));
        p2.setValor(40.0);

        pagamentosDeTeste = List.of(p1, p2);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve calcular corretamente o relatório com base nos pagamentos da data")
    void deveCalcularCorretamenteORelatorioComBaseNosPagamentosDaData() {
        when(pagamentoRepository.findAll()).thenReturn(pagamentosDeTeste);

        RelatorioDTO relatorio = relatorioService.gerarRelatorioDesempenho(dataReferencia);

        assertNotNull(relatorio);
        assertEquals(2, relatorio.getQuantidadeVeiculos());
        assertEquals(2.25, relatorio.getTempoMedioEstadia(), 0.01);
        assertEquals(75.0, relatorio.getReceitaTotal(), 0.01);
        assertEquals(0.0, relatorio.getOcupacaoMedia(), 0.01);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve gerar recibo corretamente com base na placa")
    void deveGerarReciboCorretamenteComBaseNaPlaca() {
        String placa = "ABC1234";

        Pagamento pagamento = new Pagamento();
        pagamento.setPlaca(placa);
        pagamento.setHoraEntrada(LocalDateTime.of(2025, 5, 3, 9, 0));
        pagamento.setHoraSaida(LocalDateTime.of(2025, 5, 3, 11, 30));
        pagamento.setValor(30.0);
        List<Pagamento> pagamentoList = List.of(pagamento);

        when(pagamentoRepository.findAll()).thenReturn(pagamentoList);

        var recibo = relatorioService.gerarRecibo(placa);

        assertNotNull(recibo);
        assertEquals("ABC1234", recibo.getPlaca());
        assertEquals(LocalDateTime.of(2025, 5, 3, 9, 0), recibo.getEntrada());
        assertEquals(LocalDateTime.of(2025, 5, 3, 11, 30), recibo.getSaida());
        assertEquals(30.0, recibo.getValorTotal(), 0.01);
    }
}