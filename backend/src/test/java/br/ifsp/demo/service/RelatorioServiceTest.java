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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private RelatorioService relatorioServiceSpy;

    @BeforeEach
    void setUp() {
        RelatorioService realService = new RelatorioService(pagamentoRepository, registroEntradaRepository);
        relatorioServiceSpy = Mockito.spy(realService);
    }

    @Nested
    @DisplayName("TDD Tests")
    class TddTests {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve calcular corretamente o relatório com base nos pagamentos da data")
        void deveCalcularCorretamenteORelatorioComBaseNosPagamentosDaData() {
            LocalDate dataTeste = LocalDate.of(2025, 5, 3);

            Veiculo v1Mock = Mockito.mock(Veiculo.class);
            when(v1Mock.getPlaca()).thenReturn("ABC6969");
            RegistroEntrada re1Mock = Mockito.mock(RegistroEntrada.class);
            when(re1Mock.getVeiculo()).thenReturn(v1Mock);
            when(re1Mock.getHoraEntrada()).thenReturn(LocalDateTime.of(2025, 5, 3, 10, 0));

            Veiculo v2Mock = Mockito.mock(Veiculo.class);
            when(v2Mock.getPlaca()).thenReturn("XYZ6969");
            RegistroEntrada re2Mock = Mockito.mock(RegistroEntrada.class);
            when(re2Mock.getVeiculo()).thenReturn(v2Mock);
            when(re2Mock.getHoraEntrada()).thenReturn(LocalDateTime.of(2025, 5, 3, 14, 0));

            CalculadoraDeTarifa calc = new CalculadoraTempoPermanencia(new ValorPermanencia());
            Pagamento p1 = new Pagamento(re1Mock, LocalDateTime.of(2025, 5, 3, 12, 0), calc);
            Pagamento p2 = new Pagamento(re2Mock, LocalDateTime.of(2025, 5, 3, 16, 30), calc);

            List<Pagamento> pagamentosDeTeste = List.of(p1, p2);

            when(pagamentoRepository.findAll()).thenReturn(pagamentosDeTeste);

            double minutosOcupadosP1 = Duration.between(p1.getHoraEntrada(), p1.getHoraSaida()).toMinutes();
            double minutosOcupadosP2 = Duration.between(p2.getHoraEntrada(), p2.getHoraSaida()).toMinutes();
            double totalMinutosOcupados = minutosOcupadosP1 + minutosOcupadosP2;

            long totalMinutosNoDia = 1440;
            int numeroVagas = 200;
            double fracaoOcupacao = totalMinutosOcupados / (double) (totalMinutosNoDia * numeroVagas);
            double ocupacaoMediaEsperada = (double) Math.round(fracaoOcupacao * 100.0) / 100.0;


            RelatorioDTO relatorio = relatorioServiceSpy.gerarRelatorioDesempenho(dataTeste);

            assertNotNull(relatorio);
            assertEquals(2, relatorio.quantidade());
            assertEquals(2.25, relatorio.tempoMedioHoras(), 0.01);
            assertEquals(p1.getValor() + p2.getValor(), relatorio.receitaTotal(), 0.01);
            assertEquals(ocupacaoMediaEsperada, relatorio.ocupacaoMedia(), 0.02);
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

    @Nested
    @DisplayName("Testes funcionais")
    class TestesFuncionais {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve gerar CSV corretamente com dados válidos")
        void gerarRelatorioCSV_comDadosValidos_retornaStringCSVCorreta() {
            LocalDate dataTeste = LocalDate.of(2025, 6, 1);
            RelatorioDTO mockRelatorioDto = new RelatorioDTO(
                    10,
                    2.5,
                    250.75,
                    0.65
            );

            doReturn(mockRelatorioDto).when(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);

            String csvResult = relatorioServiceSpy.gerarRelatorioCSV(dataTeste);

            assertNotNull(csvResult);

            String expectedHeader = "Métrica,Valor";
            String expectedDataLine = "Data," + dataTeste.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String expectedReceitaLine = "Receita Total,\"R$ " + String.format("%.2f", mockRelatorioDto.receitaTotal()) + "\"";
            String expectedQuantidadeLine = "Quantidade de Veículos," + mockRelatorioDto.quantidade();
            String expectedTempoMedioLine = "Tempo Médio (horas),\"" + String.format("%.2f", mockRelatorioDto.tempoMedioHoras()) + "\"";
            String expectedOcupacaoLine = "Ocupação Média,\"" + String.format("%.2f%%", mockRelatorioDto.ocupacaoMedia() * 100) + "\"";

            String[] lines = csvResult.split("\n");
            assertEquals(expectedHeader, lines[0].trim());
            assertEquals(expectedDataLine, lines[1].trim());
            assertEquals(expectedReceitaLine, lines[2].trim());
            assertEquals(expectedQuantidadeLine, lines[3].trim());
            assertEquals(expectedTempoMedioLine, lines[4].trim());
            assertEquals(expectedOcupacaoLine, lines[5].trim());

            verify(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve lançar RuntimeException encapsulada quando gerarRelatorioDesempenho falhar")
        void gerarRelatorioCSV_quandoGerarRelatorioDesempenhoFalha_lancaRuntimeException() {
            LocalDate dataTeste = LocalDate.of(2025, 6, 1);
            RuntimeException causaDaFalha = new RuntimeException("Falha simulada ao gerar desempenho");

            doThrow(causaDaFalha).when(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);

            RuntimeException exceptionLancada = assertThrows(RuntimeException.class, () -> {
                relatorioServiceSpy.gerarRelatorioCSV(dataTeste);
            });

            assertEquals("Erro ao gerar CSV", exceptionLancada.getMessage());
            assertSame(causaDaFalha, exceptionLancada.getCause());
            verify(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);
        }

    }

}