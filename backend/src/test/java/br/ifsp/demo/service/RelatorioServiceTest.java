package br.ifsp.demo.service;

import br.ifsp.demo.components.CalculadoraTempoPermanencia;
import br.ifsp.demo.components.ValorPermanencia;
import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    @Nested
    @DisplayName("TDD Tests")
    class TddTests {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve calcular corretamente o relatório com base nos pagamentos da data")
        void deveCalcularCorretamenteORelatorioComBaseNosPagamentosDaData() {
            Pagamento p1 = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo("ABC6969", "carro", "carroA", "branco")),
                    LocalDateTime.of(2025, 5, 3, 10, 0),
                    LocalDateTime.of(2025, 5, 3, 12, 0),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            Pagamento p2 = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo("XYZ6969", "carro", "carroB", "preto")),
                    LocalDateTime.of(2025, 5, 3, 14, 0),
                    LocalDateTime.of(2025, 5, 3, 16, 30),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            List<Pagamento> pagamentosDeTeste = List.of(p1, p2);

            double minutosOcupadosTotal = Duration.between(p1.getHoraEntrada(), p1.getHoraSaida()).toMinutes()
                    + Duration.between(p2.getHoraEntrada(), p2.getHoraSaida()).toMinutes();
            double ocupacaoEsperada = minutosOcupadosTotal / (1440.0 * 200);

            when(pagamentoRepository.findAll()).thenReturn(pagamentosDeTeste);

            RelatorioDTO relatorio = relatorioService.gerarRelatorioDesempenho(LocalDate.of(2025, 5, 3));

            assertNotNull(relatorio);
            assertEquals(2, relatorio.quantidade());
            assertEquals(2.25, relatorio.tempoMedioHoras(), 0.01);
            assertEquals(44.0, relatorio.receitaTotal(), 0.01);
            assertEquals(ocupacaoEsperada, relatorio.ocupacaoMedia(), 0.01);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve gerar recibo corretamente com base na placa")
        void deveGerarReciboCorretamenteComBaseNaPlaca() {
            String placa = "ABC1234";

            Pagamento pagamento = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo(placa, "carro", "carroA", "branco")),
                    LocalDateTime.of(2025, 5, 3, 9, 0),
                    LocalDateTime.of(2025, 5, 3, 11, 30),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));
            List<Pagamento> pagamentoList = List.of(pagamento);

            when(pagamentoRepository.findAll()).thenReturn(pagamentoList);

            var recibo = relatorioService.gerarRecibo(placa);

            assertNotNull(recibo);
            assertEquals("ABC1234", recibo.placa());
            assertEquals(LocalDateTime.of(2025, 5, 3, 9, 0), recibo.entrada());
            assertEquals(LocalDateTime.of(2025, 5, 3, 11, 30), recibo.saida());
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve gerar histórico corretamente com base na placa")
        void deveGerarHistoricoCorretamenteComBaseNaPlaca() {
            Veiculo veiculo69 = new Veiculo("ABC6969", "carro", "carroA", "branco");

            Pagamento pagamento1 = new Pagamento(
                    new RegistroEntrada(veiculo69),
                    LocalDateTime.of(2025, 5, 3, 8, 0),
                    LocalDateTime.of(2025, 5, 3, 10, 0),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            Pagamento pagamento2 = new Pagamento(
                    new RegistroEntrada(veiculo69),
                    LocalDateTime.of(2025, 5, 4, 9, 0),
                    LocalDateTime.of(2025, 5, 4, 11, 30),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            Pagamento pagamento3 = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo("XYZ6969", "carro", "carroB", "preto")),
                    LocalDateTime.of(2025, 5, 4, 12, 0),
                    LocalDateTime.of(2025, 5, 4, 14, 0),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            List<Pagamento> pagamentoList = List.of(pagamento1, pagamento2, pagamento3);

            when(pagamentoRepository.findAll()).thenReturn(pagamentoList);

            List<HistoricoDTO> historico = relatorioService.gerarHistorico(veiculo69.getPlaca());

            assertNotNull(historico);
            assertEquals(2, historico.size());

            assertEquals("ABC6969", historico.get(1).placa());
            assertEquals(LocalDateTime.of(2025, 5, 3, 8, 0), historico.get(1).horaEntrada());
            assertEquals(LocalDateTime.of(2025, 5, 3, 10, 0), historico.get(1).horaSaida());
            assertEquals(18.0, historico.get(1).valor(), 0.01);

            assertEquals("ABC6969", historico.get(0).placa());
            assertEquals(LocalDateTime.of(2025, 5, 4, 9, 0), historico.get(0).horaEntrada());
            assertEquals(LocalDateTime.of(2025, 5, 4, 11, 30), historico.getFirst().horaSaida());
            assertEquals(26.0, historico.getFirst().valor(), 0.01);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar corretamente o número de vagas disponíveis")
        void deveRetornarCorretamenteONumeroDeVagasDisponiveis() {
            when(registroEntradaRepository.count()).thenReturn(2L);

            int vagasDisponiveis = relatorioService.vagasDisponiveis();

            assertEquals(198, vagasDisponiveis);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar corretamente o número de vagas ocupadas")
        void deveRetornarCorretamenteONumeroDeVagasOcupadas() {
            when(registroEntradaRepository.count()).thenReturn(2L);

            int vagasOcupadas = relatorioService.vagasOcupadas();

            assertEquals(2, vagasOcupadas);
        }
    }
}