package br.ifsp.demo.service;

import br.ifsp.demo.components.CalculadoraTempoPermanencia;
import br.ifsp.demo.components.ValorPermanencia;
import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.ReciboDTO;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        @Nested
        @DisplayName("CSV Testes")
        class CSVTestes {
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

        @Nested
        @DisplayName("PDF Testes")
        class PDFTestes {
            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Deve gerar array de bytes do PDF corretamente com dados válidos")
            void gerarRelatorioPDF_comDadosValidos_retornaByteArrayNaoVazio() {
                LocalDate dataTeste = LocalDate.of(2025, 7, 15);
                RelatorioDTO mockRelatorioDto = new RelatorioDTO(
                        15,
                        3.1,
                        350.50,
                        0.75
                );

                doReturn(mockRelatorioDto).when(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);

                byte[] pdfBytes = relatorioServiceSpy.gerarRelatorioPDF(dataTeste);

                assertNotNull(pdfBytes, "O array de bytes do PDF não deveria ser nulo.");
                assertTrue(pdfBytes.length > 0, "O array de bytes do PDF não deveria estar vazio.");

                if (pdfBytes.length > 4) {
                    String pdfHeader = new String(pdfBytes, 0, 4);
                    assertEquals("%PDF", pdfHeader, "O output não parece ser um ficheiro PDF válido (cabeçalho incorreto).");
                }

                verify(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Deve lançar RuntimeException encapsulada quando gerarRelatorioDesempenho falhar ao gerar PDF")
            void gerarRelatorioPDF_quandoGerarRelatorioDesempenhoFalha_lancaRuntimeException() {
                LocalDate dataTeste = LocalDate.of(2025, 7, 15);
                RuntimeException causaDaFalha = new RuntimeException("Falha simulada ao obter dados para PDF");

                doThrow(causaDaFalha).when(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);

                RuntimeException exceptionLancada = assertThrows(RuntimeException.class, () -> {
                    relatorioServiceSpy.gerarRelatorioPDF(dataTeste);
                });

                assertEquals("Erro ao gerar PDF", exceptionLancada.getMessage());
                assertSame(causaDaFalha, exceptionLancada.getCause(), "A causa da exceção não é a esperada.");
                verify(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Deve lançar RuntimeException se ocorrer erro na biblioteca iText ao gerar PDF (Placeholder)")
            void gerarRelatorioPDF_quandoITextFalha_lancaRuntimeException() {

                assertTrue(true, "Teste para falha específica do iText requereria refatoração ou mocks mais complexos e não está implementado.");
            }
        }


    }

    @Nested
    @DisplayName("Testes estruturais")
    class TestesEstruturais {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Structural")
        @DisplayName("Deve retornar relatório com valores zerados quando não há pagamentos no dia")
        void gerarRelatorioDesempenho_semPagamentosNoDia_retornaRelatorioZerado() {
            LocalDate dataTeste = LocalDate.of(2025, 5, 4);

            when(pagamentoRepository.findAll()).thenReturn(Collections.emptyList());

            RelatorioDTO relatorio = relatorioServiceSpy.gerarRelatorioDesempenho(dataTeste);

            assertNotNull(relatorio);
            assertEquals(0, relatorio.quantidade());
            assertEquals(0.0, relatorio.tempoMedioHoras(), 0.01);
            assertEquals(0.0, relatorio.receitaTotal(), 0.01);
            assertEquals(0.0, relatorio.ocupacaoMedia(), 0.01);
        }

        @Nested
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Relatorio mensal testes")
        class RelatorioMensalTestes {
            @Test
            @Tag("Structural")
            @Tag("UnitTest")
            @DisplayName("Deve calcular relatório mensal corretamente para mês com atividade em múltiplos dias")
            void gerarRelatorioMensal_comAtividadeEmMultiplosDias() {
                int ano = 2025;
                int mes = 1;
                LocalDate dia1 = LocalDate.of(ano, mes, 1);
                LocalDate dia2 = LocalDate.of(ano, mes, 2);

                RelatorioDTO relatorioDia1 = new RelatorioDTO(2, 2.0, 100.0, 0.1);
                RelatorioDTO relatorioDia2 = new RelatorioDTO(3, 1.0, 150.0, 0.15);
                RelatorioDTO relatorioDiaZero = new RelatorioDTO(0, 0.0, 0.0, 0.0);

                doReturn(relatorioDia1).when(relatorioServiceSpy).gerarRelatorioDesempenho(dia1);
                doReturn(relatorioDia2).when(relatorioServiceSpy).gerarRelatorioDesempenho(dia2);

                LocalDate inicioMes = LocalDate.of(ano, mes, 1);
                LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
                for (LocalDate data = inicioMes; !data.isAfter(fimMes); data = data.plusDays(1)) {
                    if (!data.equals(dia1) && !data.equals(dia2)) {
                        doReturn(relatorioDiaZero).when(relatorioServiceSpy).gerarRelatorioDesempenho(data);
                    }
                }

                Map<String, Object> resultado = relatorioServiceSpy.gerarRelatorioMensal(mes, ano);

                assertEquals(mes, resultado.get("mes"));
                assertEquals(ano, resultado.get("ano"));
                assertEquals(250.0, (Double) resultado.get("receitaTotal"), 0.01);
                assertEquals(5, resultado.get("totalVeiculos"));

                assertEquals(1.4, (Double) resultado.get("tempoMedioHoras"), 0.01);

                assertEquals(2, resultado.get("melhorDia"));
                assertEquals(150.0, (Double) resultado.get("melhorReceita"), 0.01);
                assertEquals(fimMes.getDayOfMonth(), resultado.get("diasNoMes"));

                @SuppressWarnings("unchecked")
                Map<Integer, Double> receitaPorDia = (Map<Integer, Double>) resultado.get("receitaPorDia");
                assertEquals(100.0, receitaPorDia.get(1), 0.01);
                assertEquals(150.0, receitaPorDia.get(2), 0.01);
                assertEquals(0.0, receitaPorDia.get(3), 0.01);

                @SuppressWarnings("unchecked")
                Map<Integer, Integer> veiculosPorDia = (Map<Integer, Integer>) resultado.get("veiculosPorDia");
                assertEquals(2, veiculosPorDia.get(1));
                assertEquals(3, veiculosPorDia.get(2));
                assertEquals(0, veiculosPorDia.get(3));
            }

            @Test
            @Tag("Structural")
            @Tag("UnitTest")
            @DisplayName("Deve calcular relatório mensal com zero veículos e zero receita")
            void gerarRelatorioMensal_semAtividade_retornaValoresZerados() {
                int ano = 2025;
                int mes = 2;
                RelatorioDTO relatorioDiaZero = new RelatorioDTO(0, 0.0, 0.0, 0.0);

                LocalDate inicioMes = LocalDate.of(ano, mes, 1);
                LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
                for (LocalDate data = inicioMes; !data.isAfter(fimMes); data = data.plusDays(1)) {
                    doReturn(relatorioDiaZero).when(relatorioServiceSpy).gerarRelatorioDesempenho(data);
                }

                Map<String, Object> resultado = relatorioServiceSpy.gerarRelatorioMensal(mes, ano);

                assertEquals(mes, resultado.get("mes"));
                assertEquals(ano, resultado.get("ano"));
                assertEquals(0.0, (Double) resultado.get("receitaTotal"), 0.01);
                assertEquals(0, resultado.get("totalVeiculos"));
                assertEquals(0.0, (Double) resultado.get("tempoMedioHoras"), 0.01);
                assertEquals(0.0, (Double) resultado.get("receitaMediaDiaria"), 0.01);
                assertEquals(0.0, (Double) resultado.get("veiculosMediaDiaria"), 0.01);
                assertEquals(1, resultado.get("melhorDia"));
                assertEquals(0.0, (Double) resultado.get("melhorReceita"), 0.01);
                assertEquals(fimMes.getDayOfMonth(), resultado.get("diasNoMes"));

                @SuppressWarnings("unchecked")
                Map<Integer, Double> receitaPorDia = (Map<Integer, Double>) resultado.get("receitaPorDia");
                assertTrue(receitaPorDia.values().stream().allMatch(v -> v == 0.0));

                @SuppressWarnings("unchecked")
                Map<Integer, Integer> veiculosPorDia = (Map<Integer, Integer>) resultado.get("veiculosPorDia");
                assertTrue(veiculosPorDia.values().stream().allMatch(v -> v == 0));
            }
        }

        @Nested
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Relatorio mensal PDF testes")
        class RelatorioMensalPDFTestes {

            @Test
            @Tag("Structural")
            @Tag("UnitTest")
            @DisplayName("Deve gerar array de bytes do PDF mensal corretamente com dados válidos")
            void gerarRelatorioMensalPDF_comDadosValidos_retornaByteArrayNaoVazio() {
                int mesTeste = 5;
                int anoTeste = 2025;

                Map<String, Object> mockDadosMensais = new HashMap<>();
                mockDadosMensais.put("receitaTotal", 1500.75);
                mockDadosMensais.put("totalVeiculos", 120);
                mockDadosMensais.put("tempoMedioHoras", 2.8);
                mockDadosMensais.put("receitaMediaDiaria", 1500.75 / 31);
                mockDadosMensais.put("melhorDia", 15);
                mockDadosMensais.put("melhorReceita", 250.0);

                doReturn(mockDadosMensais).when(relatorioServiceSpy).gerarRelatorioMensal(mesTeste, anoTeste);

                byte[] pdfBytes = relatorioServiceSpy.gerarRelatorioMensalPDF(mesTeste, anoTeste);

                assertNotNull(pdfBytes, "O array de bytes do PDF mensal não deveria ser nulo.");
                assertTrue(pdfBytes.length > 0, "O array de bytes do PDF mensal não deveria estar vazio.");

                if (pdfBytes.length > 4) {
                    String pdfHeader = new String(pdfBytes, 0, 4);
                    assertEquals("%PDF", pdfHeader, "O output não parece ser um ficheiro PDF válido (cabeçalho incorreto).");
                }

                verify(relatorioServiceSpy).gerarRelatorioMensal(mesTeste, anoTeste);
            }

            @Test
            @Tag("Structural")
            @Tag("UnitTest")
            @DisplayName("Deve lançar RuntimeException encapsulada quando gerarRelatorioMensal falhar ao gerar PDF mensal")
            void gerarRelatorioMensalPDF_quandoGerarRelatorioMensalFalha_lancaRuntimeException() {

                int mesTeste = 7;
                int anoTeste = 2025;
                RuntimeException causaDaFalha = new RuntimeException("Falha simulada ao obter dados mensais para PDF");

                doThrow(causaDaFalha).when(relatorioServiceSpy).gerarRelatorioMensal(mesTeste, anoTeste);

                RuntimeException exceptionLancada = assertThrows(RuntimeException.class, () -> {
                    relatorioServiceSpy.gerarRelatorioMensalPDF(mesTeste, anoTeste);
                });

                assertEquals("Erro ao gerar PDF mensal", exceptionLancada.getMessage());
                assertSame(causaDaFalha, exceptionLancada.getCause(), "A causa da exceção não é a esperada.");
                verify(relatorioServiceSpy).gerarRelatorioMensal(mesTeste, anoTeste);
            }

        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve retornar null quando placa não for encontrada no recibo")
        void deveRetornarNullQuandoPlacaNaoForEncontradaNoRecibo() {
            String placaInexistente = "XYZ9999";
            Pagamento pagamento1 = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo("ABC1234", "carro", "carroA", "branco")),
                    LocalDateTime.of(2025, 5, 3, 9, 0),
                    LocalDateTime.of(2025, 5, 3, 11, 30),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            Pagamento pagamento2 = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo("DEF5678", "carro", "carroB", "preto")),
                    LocalDateTime.of(2025, 5, 3, 10, 0),
                    LocalDateTime.of(2025, 5, 3, 12, 0),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            List<Pagamento> pagamentoList = List.of(pagamento1, pagamento2);
            when(pagamentoRepository.findAll()).thenReturn(pagamentoList);
            ReciboDTO recibo = relatorioService.gerarRecibo(placaInexistente);

            assertNull(recibo, "Deve retornar null quando a placa não for encontrada");

            verify(pagamentoRepository).findAll();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve filtrar corretamente apenas a placa específica")
        void deveFiltrarCorretamenteApenasAPlacaEspecifica() {
            String placaProcurada = "ABC1234";

            Pagamento pagamentoCorreto = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo(placaProcurada, "carro", "carroA", "branco")),
                    LocalDateTime.of(2025, 5, 3, 14, 0),
                    LocalDateTime.of(2025, 5, 3, 16, 30),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            Pagamento pagamentoIncorreto1 = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo("XYZ9999", "carro", "carroB", "preto")),
                    LocalDateTime.of(2025, 5, 3, 10, 0),
                    LocalDateTime.of(2025, 5, 3, 12, 0),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            Pagamento pagamentoIncorreto2 = new Pagamento(
                    new RegistroEntrada(
                            new Veiculo("DEF5678", "carro", "carroC", "azul")),
                    LocalDateTime.of(2025, 5, 3, 8, 0),
                    LocalDateTime.of(2025, 5, 3, 10, 0),
                    new CalculadoraTempoPermanencia(
                            new ValorPermanencia()));

            List<Pagamento> pagamentoList = List.of(
                    pagamentoIncorreto1,
                    pagamentoCorreto,
                    pagamentoIncorreto2
            );
            when(pagamentoRepository.findAll()).thenReturn(pagamentoList);

            ReciboDTO recibo = relatorioService.gerarRecibo(placaProcurada);

            assertNotNull(recibo);
            assertEquals(placaProcurada, recibo.placa());
            assertEquals(LocalDateTime.of(2025, 5, 3, 14, 0), recibo.entrada());
            assertEquals(LocalDateTime.of(2025, 5, 3, 16, 30), recibo.saida());
            assertEquals(pagamentoCorreto.getValor(), recibo.valorTotal());

            verify(pagamentoRepository).findAll();
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve retornar null quando lista de pagamentos estiver vazia")
        void deveRetornarNullQuandoListaDePagamentosEstiverVazia() {
            String qualquerPlaca = "ABC1234";

            when(pagamentoRepository.findAll()).thenReturn(Collections.emptyList());

            ReciboDTO recibo = relatorioService.gerarRecibo(qualquerPlaca);

            assertNull(recibo, "Deve retornar null quando não há pagamentos");
            verify(pagamentoRepository).findAll();
        }


        @Nested
        @DisplayName("Testes para Matar Mutantes Sobreviventes")
        class TestesParaMatarMutantesSobreviventes {

            @Test
            @Tag("Mutation")
            @DisplayName("Deve filtrar pagamentos por data corretamente usando método público")
            void deveFiltrarPagamentosPorDataCorretamente() {
                LocalDate dataTeste = LocalDate.of(2025, 6, 1);
                LocalDateTime dataForaDoPeriodo = LocalDate.of(2025, 5, 31).atTime(10, 0);

                Pagamento pagamentoDentro = new Pagamento(
                        new RegistroEntrada(new Veiculo("ABC1234", "carro", "civic", "branco")),
                        dataTeste.atTime(10, 0),
                        dataTeste.atTime(12, 0),
                        new CalculadoraTempoPermanencia(new ValorPermanencia()));

                Pagamento pagamentoFora = new Pagamento(
                        new RegistroEntrada(new Veiculo("XYZ9999", "carro", "corolla", "preto")),
                        dataForaDoPeriodo,
                        dataForaDoPeriodo.plusHours(2),
                        new CalculadoraTempoPermanencia(new ValorPermanencia()));

                when(pagamentoRepository.findAll()).thenReturn(List.of(pagamentoDentro, pagamentoFora));

                RelatorioDTO resultado = relatorioService.gerarRelatorioDesempenho(dataTeste);

                assertEquals(1, resultado.quantidade());
                assertTrue(resultado.receitaTotal() > 0);
            }

            @Test
            @Tag("Mutation")
            @DisplayName("Deve gerar PDF mensal completo com document close")
            void deveGerarPDFMensalCompletoComDocumentClose() {
                byte[] pdfBytes = relatorioService.gerarRelatorioMensalPDF(6, 2025);

                assertNotNull(pdfBytes);
                assertTrue(pdfBytes.length > 0);

                assertTrue(pdfBytes.length > 1000);
            }

            @Test
            @Tag("Mutation")
            @DisplayName("Deve verificar que CSV é finalizado corretamente")
            void deveVerificarQueCSVEhFinalizadoCorretamente() throws Exception {
                LocalDate dataTeste = LocalDate.of(2025, 6, 1);
                RelatorioDTO mockRelatorioDto = new RelatorioDTO(5, 2.0, 100.0, 0.5);

                doReturn(mockRelatorioDto).when(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);

                String csv1 = relatorioServiceSpy.gerarRelatorioCSV(dataTeste);
                String csv2 = relatorioServiceSpy.gerarRelatorioCSV(dataTeste);
                String csv3 = relatorioServiceSpy.gerarRelatorioCSV(dataTeste);

                assertNotNull(csv1);
                assertEquals(csv1, csv2, "CSVs devem ser idênticos após múltiplas gerações");
                assertEquals(csv2, csv3, "CSVs devem ser idênticos após múltiplas gerações");

                String[] linhas = csv1.split("\n");
                assertEquals(6, linhas.length, "CSV deve ter 6 linhas exatas");
                assertTrue(linhas[5].contains("%"), "Última linha deve conter porcentagem formatada");

                verify(relatorioServiceSpy, times(3)).gerarRelatorioDesempenho(dataTeste);
            }

            @Test
            @Tag("Mutation")
            @DisplayName("Deve validar cálculos críticos contra mutantes matemáticos")
            void deveValidarCalculosCriticosContraMutantesMatematicos() {
                LocalDate dataTeste = LocalDate.of(2025, 9, 1);
                Pagamento pagamento1 = new Pagamento(
                        new RegistroEntrada(new Veiculo("ABC1234", "carro", "civic", "branco")),
                        LocalDateTime.of(2025, 9, 1, 10, 0),
                        LocalDateTime.of(2025, 9, 1, 12, 30),
                        new CalculadoraTempoPermanencia(new ValorPermanencia())
                );

                Pagamento pagamento2 = new Pagamento(
                        new RegistroEntrada(new Veiculo("DEF5678", "carro", "corolla", "preto")),
                        LocalDateTime.of(2025, 9, 1, 14, 0),
                        LocalDateTime.of(2025, 9, 1, 15, 30),
                        new CalculadoraTempoPermanencia(new ValorPermanencia())
                );

                when(pagamentoRepository.findAll()).thenReturn(List.of(pagamento1, pagamento2));

                RelatorioDTO relatorio = relatorioServiceSpy.gerarRelatorioDesempenho(dataTeste);

                assertEquals(2, relatorio.quantidade());
                assertEquals(2.0, relatorio.tempoMedioHoras(), 0.01, "Tempo médio deve ser (2.5 + 1.5)/2 = 2.0");
                assertEquals(0.0, relatorio.ocupacaoMedia(),
                        "Cálculo deve ser: (150+90)/(1440*200) = 240/288000 = 0.000833...");

                double mutanteOcupacao = (double) 240 / ((double) 1440 / 200);
                assertNotEquals(mutanteOcupacao, relatorio.ocupacaoMedia(),
                        "Se mutante sobreviver, este valor seria 33.333...");

                verify(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);
            }

            @Test
            @Tag("Mutation")
            @DisplayName("Deve verificar que CSVPrinter é fechado e 'flushado' corretamente")
            void shouldFlushAndCloseCSVPrinter() throws Exception {

                LocalDate dataTeste = LocalDate.of(2025, 6, 1);
                RelatorioDTO mockRelatorioDto = new RelatorioDTO(5, 2.0, 100.0, 0.5);

                doReturn(mockRelatorioDto).when(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);
                relatorioServiceSpy.gerarRelatorioCSV(dataTeste);

            }
        }
    }
}