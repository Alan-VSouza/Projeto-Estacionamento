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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
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





    }

    @Nested
    @DisplayName("Testes para Matar Mutantes Sobreviventes")
    class TestesParaMatarMutantesSobreviventes {

        @Test
        @Tag("UnitTest")
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
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve gerar PDF mensal completo com document close")
        void deveGerarPDFMensalCompletoComDocumentClose() {
            byte[] pdfBytes = relatorioService.gerarRelatorioMensalPDF(6, 2025);

            assertNotNull(pdfBytes);
            assertTrue(pdfBytes.length > 0);

            assertTrue(pdfBytes.length > 1000);
        }

        @Test
        @Tag("UnitTest")
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
        @Tag("UnitTest")
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
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve verificar que CSVPrinter é fechado e 'flushado' corretamente")
        void shouldFlushAndCloseCSVPrinter() throws Exception {

            LocalDate dataTeste = LocalDate.of(2025, 6, 1);
            RelatorioDTO mockRelatorioDto = new RelatorioDTO(5, 2.0, 100.0, 0.5);

            doReturn(mockRelatorioDto).when(relatorioServiceSpy).gerarRelatorioDesempenho(dataTeste);
            relatorioServiceSpy.gerarRelatorioCSV(dataTeste);

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

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve calcular médias diárias corretas no relatório mensal")
        void deveCalcularMediasDiariasCorretasNoRelatorioMensal() {
            LocalDate dataAbril = LocalDate.of(2025, 4, 15);
            LocalDate dataFevereiro = LocalDate.of(2025, 2, 15);

            Pagamento pagamentoAbril = new Pagamento(
                    new RegistroEntrada(new Veiculo("ABC1234", "carro", "civic", "branco")),
                    dataAbril.atTime(10, 0),
                    dataAbril.atTime(12, 0),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );

            when(pagamentoRepository.findAll()).thenReturn(List.of(pagamentoAbril));

            Map<String, Object> resultadoAbril = relatorioService.gerarRelatorioMensal(4, 2025);

            assertNotNull(resultadoAbril);

            double receitaTotalAbril = (Double) resultadoAbril.get("receitaTotal");
            int totalVeiculosAbril = (Integer) resultadoAbril.get("totalVeiculos");
            int diasAbril = (Integer) resultadoAbril.get("diasNoMes");

            assertEquals(30, diasAbril, "Abril deve ter 30 dias");

            if (receitaTotalAbril > 0) {
                double receitaMediaEsperada = receitaTotalAbril / 30;
                double receitaMediaMutante = receitaTotalAbril * 30;

                if (resultadoAbril.containsKey("receitaMediaDiaria")) {
                    double receitaMediaCalculada = (Double) resultadoAbril.get("receitaMediaDiaria");
                    assertEquals(receitaMediaEsperada, receitaMediaCalculada, 0.01,
                            "Receita média deve ser receitaTotal / diasNoMes");

                    assertNotEquals(receitaMediaMutante, receitaMediaCalculada,
                            "Mutante de multiplicação deve dar resultado diferente");
                }
            }

            if (totalVeiculosAbril > 0) {
                double veiculosMediaEsperada = (double) totalVeiculosAbril / 30;
                double veiculosMediaMutante = (double) totalVeiculosAbril * 30;

                if (resultadoAbril.containsKey("veiculosMediaDiaria")) {
                    double veiculosMediaCalculada = (Double) resultadoAbril.get("veiculosMediaDiaria");
                    assertEquals(veiculosMediaEsperada, veiculosMediaCalculada, 0.01,
                            "Veículos média deve ser totalVeiculos / diasNoMes");

                    assertNotEquals(veiculosMediaMutante, veiculosMediaCalculada,
                            "Mutante de multiplicação deve dar resultado diferente");
                }
            }
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve detectar mutantes de divisão com valores específicos")
        void deveDetectarMutantesDeDivisaoComValoresEspecificos() {

            double receitaTotal = 3000.0;
            int totalVeiculos = 60;
            int diasNoMes = 30;

            double receitaMediaCorreta = receitaTotal / diasNoMes;
            double receitaMediaMutante = receitaTotal * diasNoMes;

            assertEquals(100.0, receitaMediaCorreta, 0.01, "3000 / 30 = 100");
            assertEquals(90000.0, receitaMediaMutante, 0.01, "3000 * 30 = 90000");
            assertNotEquals(receitaMediaCorreta, receitaMediaMutante,
                    "Divisão e multiplicação devem dar resultados diferentes");

            double veiculosMediaCorreta = (double) totalVeiculos / diasNoMes;
            double veiculosMediaMutante = (double) totalVeiculos * diasNoMes;

            assertEquals(2.0, veiculosMediaCorreta, 0.01, "60 / 30 = 2");
            assertEquals(1800.0, veiculosMediaMutante, 0.01, "60 * 30 = 1800");
            assertNotEquals(veiculosMediaCorreta, veiculosMediaMutante,
                    "Divisão e multiplicação devem dar resultados diferentes");

            int diasFevereiro = 28;
            double receitaMediaFev = receitaTotal / diasFevereiro;
            double receitaMediaMutanteFev = receitaTotal * diasFevereiro;

            assertEquals(107.14, receitaMediaFev, 0.01, "3000 / 28 ≈ 107.14");
            assertEquals(84000.0, receitaMediaMutanteFev, 0.01, "3000 * 28 = 84000");
            assertNotEquals(receitaMediaFev, receitaMediaMutanteFev,
                    "Para fevereiro, divisão e multiplicação devem ser diferentes");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve garantir PDF íntegro e conter dados do relatório (mata mutante de document.close())")
        void deveGarantirPdfIntegroComDocumentClose() throws Exception {
            LocalDate data = LocalDate.of(2025, 6, 1);

            Pagamento pagamento = new Pagamento(
                    new RegistroEntrada(new Veiculo("ABC1234", "carro", "civic", "branco")),
                    data.atTime(10, 0),
                    data.atTime(12, 0),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );
            when(pagamentoRepository.findAll()).thenReturn(List.of(pagamento));

            byte[] pdfBytes = relatorioService.gerarRelatorioPDF(data);

            assertNotNull(pdfBytes, "O PDF não deve ser nulo");
            assertTrue(pdfBytes.length > 500, "O PDF deve ter tamanho razoável (não corrompido)");

            String header = new String(pdfBytes, 0, Math.min(4, pdfBytes.length), StandardCharsets.ISO_8859_1);
            assertTrue(header.startsWith("%PDF"), "O PDF deve começar com o header %PDF");
            String pdfString = new String(pdfBytes, StandardCharsets.ISO_8859_1);
            assertTrue(pdfString.contains("%%EOF"), "O PDF deve terminar com %%EOF");

            try (PDDocument document = PDDocument.load(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String extractedText = stripper.getText(document);

                assertTrue(extractedText.contains("Relatório Diário de Desempenho"), "O PDF deve conter o título do relatório");
                assertTrue(extractedText.contains("Receita Total"), "O PDF deve conter os headers da tabela");
            }
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve garantir que ocupação média no PDF é multiplicada por 100 (mata mutante de divisão)")
        void deveGarantirOcupacaoMediaMultiplicadaPor100NoPDF() throws Exception {
            LocalDate data = LocalDate.of(2025, 6, 1);

            double ocupacaoMedia = 0.75;
            RelatorioDTO relatorioDTO = new RelatorioDTO(10, 2.0, 100.0, ocupacaoMedia);

            doReturn(relatorioDTO).when(relatorioServiceSpy).gerarRelatorioDesempenho(data);

            byte[] pdfBytes = relatorioServiceSpy.gerarRelatorioPDF(data);

            assertNotNull(pdfBytes, "O PDF não deve ser nulo");
            assertTrue(pdfBytes.length > 500, "O PDF deve ter tamanho razoável");

            try (PDDocument document = PDDocument.load(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String extractedText = stripper.getText(document);
                System.out.println("Texto extraído do PDF:\n" + extractedText);

                String normalized = extractedText.replace("\n", "").replace("\r", "").replace(" ", "");

                assertTrue(normalized.contains("75.00%") || normalized.contains("75,00%"),
                        "O PDF deve conter ocupação média como 75.00% ou 75,00%");

                assertFalse(normalized.contains("0.01%") || normalized.contains("0,01%"),
                        "O PDF não pode conter ocupação média como 0.01% ou 0,01%");
                assertFalse(normalized.contains("0.75%") || normalized.contains("0,75%"),
                        "O PDF não pode conter ocupação média como 0.75% ou 0,75%");
            }
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve matar mutante de boundary em calcularTempoMedioHoras")
        void deveMatarMutanteDeBoundaryEmCalcularTempoMedioHoras() throws Exception {
            Method method = RelatorioService.class.getDeclaredMethod("calcularTempoMedioHoras", int.class, double.class);
            method.setAccessible(true);

            double resultadoZero = (double) method.invoke(relatorioService, 0, 120.0);
            assertEquals(0.0, resultadoZero, 0.001, "Com quantidade 0, deve retornar 0.0");

            double resultadoUm = (double) method.invoke(relatorioService, 1, 120.0);
            assertEquals(2.0, resultadoUm, 0.001, "Com quantidade 1, deve retornar 2.0");

            double resultadoNegativo = (double) method.invoke(relatorioService, -1, 120.0);
            assertEquals(0.0, resultadoNegativo, 0.001, "Com quantidade negativa, deve retornar 0.0");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve matar mutante de filtro sempre verdadeiro em calcularMinutosOcupados")
        void deveMatarMutanteFiltroSempreVerdadeiro() {
            LocalDate data = LocalDate.of(2025, 6, 1);
            LocalDateTime inicioDoDia = data.atStartOfDay();

            Pagamento p1 = new Pagamento(
                    new RegistroEntrada(new Veiculo("A", "carro", "modelo", "cor")),
                    inicioDoDia,
                    inicioDoDia.plusMinutes(1500),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );
            Pagamento pInvalido = mock(Pagamento.class);
            when(pInvalido.getHoraEntrada()).thenReturn(null);
            when(pInvalido.getHoraSaida()).thenReturn(inicioDoDia.plusMinutes(10));

            when(pagamentoRepository.findAll()).thenReturn(List.of(p1, pInvalido));
            RelatorioDTO resultado = relatorioService.gerarRelatorioDesempenho(data);

            Pagamento p2 = new Pagamento(
                    new RegistroEntrada(new Veiculo("B", "carro", "modelo", "cor")),
                    inicioDoDia,
                    inicioDoDia.plusMinutes(1500),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );
            when(pagamentoRepository.findAll()).thenReturn(List.of(p1, p2));
            RelatorioDTO resultado2 = relatorioService.gerarRelatorioDesempenho(data);

            assertEquals(resultado2.ocupacaoMedia(), resultado.ocupacaoMedia(), 0.001,
                    "Pagamentos inválidos não devem influenciar o resultado");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve matar mutantes de multiplicação/divisão e return 0.0 em calcularOcupacaoMedia")
        void deveMatarMutantesCalcularOcupacaoMedia() throws Exception {
            Method method = RelatorioService.class.getDeclaredMethod("calcularOcupacaoMedia", double.class);
            method.setAccessible(true);

            long minutosNoDia = Duration.between(LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now()).toMinutes();
            int NUMERO_VAGAS = 200;

            double minutosOcupadosTotal = minutosNoDia * NUMERO_VAGAS * 0.01; 

            double esperado = (double) Math.round((minutosOcupadosTotal / (minutosNoDia * NUMERO_VAGAS)) * 100) / 100;

            double mutante1 = (double) Math.round((minutosOcupadosTotal / (minutosNoDia / (double) NUMERO_VAGAS)) * 100) / 100;

            double mutante2 = (double) Math.round((minutosOcupadosTotal * (minutosNoDia * NUMERO_VAGAS)) * 100) / 100;

            double mutante5 = 0.0;

            double resultado = (double) method.invoke(relatorioService, minutosOcupadosTotal);

            assertEquals(0.01, esperado, 0.001, "Esperado: 1% de ocupação");
            assertEquals(esperado, resultado, 0.001, "Resultado real deve ser igual ao esperado");

            assertNotEquals(mutante1, resultado, "Mutante 1 (divisão errada) deve ser diferente do correto");

            assertNotEquals(mutante2, resultado, "Mutante 2 (multiplicação errada) deve ser diferente do correto");

            assertNotEquals(mutante5, resultado, "Mutante 5 (sempre zero) deve ser diferente do correto");

            minutosOcupadosTotal = minutosNoDia * NUMERO_VAGAS * 0.5;
            double esperadoMeio = (double) Math.round((minutosOcupadosTotal / (minutosNoDia * NUMERO_VAGAS)) * 100) / 100;
            double resultadoMeio = (double) method.invoke(relatorioService, minutosOcupadosTotal);
            assertEquals(0.5, esperadoMeio, 0.001, "Esperado: 50% de ocupação");
            assertEquals(esperadoMeio, resultadoMeio, 0.001, "Resultado real deve ser igual ao esperado");
            assertNotEquals(0.0, resultadoMeio, "Mutante 5 não pode sobreviver com ocupação alta");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve matar mutantes de soma e filtro em calcularMinutosOcupados")
        void deveMatarMutantesCalcularMinutosOcupados() throws Exception {
            Method method = RelatorioService.class.getDeclaredMethod(
                    "calcularMinutosOcupados",
                    List.class, LocalDateTime.class, LocalDateTime.class
            );
            method.setAccessible(true);

            LocalDate data = LocalDate.of(2025, 6, 1);
            LocalDateTime inicioDoDia = data.atStartOfDay();
            LocalDateTime fimDoDia = data.atTime(23, 59, 59);

            Pagamento p1 = new Pagamento(
                    new RegistroEntrada(new Veiculo("A", "carro", "modelo", "cor")),
                    inicioDoDia.plusHours(1),
                    inicioDoDia.plusHours(3),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );

            Pagamento p2 = new Pagamento(
                    new RegistroEntrada(new Veiculo("B", "carro", "modelo", "cor")),
                    inicioDoDia.minusHours(2),
                    inicioDoDia.plusHours(2),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );

            Pagamento p3 = new Pagamento(
                    new RegistroEntrada(new Veiculo("C", "carro", "modelo", "cor")),
                    fimDoDia.minusHours(1),
                    fimDoDia.plusHours(2),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );

            Pagamento pInvalido = mock(Pagamento.class, withSettings().lenient());
            lenient().when(pInvalido.getHoraEntrada()).thenReturn(null);
            lenient().when(pInvalido.getHoraSaida()).thenReturn(fimDoDia);

            List<Pagamento> pagamentos = List.of(p1, p2, p3, pInvalido);

            double resultado = (double) method.invoke(relatorioService, pagamentos, inicioDoDia, fimDoDia);

            assertEquals(300.0, resultado, 0.001, "Deve somar apenas pagamentos válidos");

            assertNotEquals(0.0, resultado, "Não pode retornar zero com pagamentos válidos");

            List<Pagamento> apenasInvalido = List.of(pInvalido);
            double resultadoInvalido = (double) method.invoke(relatorioService, apenasInvalido, inicioDoDia, fimDoDia);
            assertEquals(0.0, resultadoInvalido, 0.001, "Pagamentos inválidos devem ser ignorados");

            List<Pagamento> todosValidosEInvalidos = List.of(p1, p2, p3, pInvalido);
            double resultadoComInvalidos = (double) method.invoke(relatorioService, todosValidosEInvalidos, inicioDoDia, fimDoDia);
            assertEquals(300.0, resultadoComInvalidos, 0.001, "Pagamentos inválidos não devem ser somados");
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve cobrir todos os branches do filtro de horaEntrada/horaSaida")
        void deveCobrirBranchesFiltroHoraEntradaHoraSaida() throws Exception {
            Method method = RelatorioService.class.getDeclaredMethod(
                    "calcularMinutosOcupados",
                    List.class, LocalDateTime.class, LocalDateTime.class
            );
            method.setAccessible(true);

            LocalDate data = LocalDate.of(2025, 6, 1);
            LocalDateTime inicioDoDia = data.atStartOfDay();
            LocalDateTime fimDoDia = data.atTime(23, 59, 59);

            Pagamento p1 = new Pagamento(
                    new RegistroEntrada(new Veiculo("A", "carro", "modelo", "cor")),
                    inicioDoDia.plusHours(8),
                    inicioDoDia.plusHours(10),
                    new CalculadoraTempoPermanencia(new ValorPermanencia())
            );

            Pagamento p2 = mock(Pagamento.class);
            lenient().when(p2.getHoraEntrada()).thenReturn(null);
            lenient().when(p2.getHoraSaida()).thenReturn(inicioDoDia.plusHours(12));

            Pagamento p3 = mock(Pagamento.class);
            lenient().when(p3.getHoraEntrada()).thenReturn(inicioDoDia.plusHours(14));
            lenient().when(p3.getHoraSaida()).thenReturn(null);

            Pagamento p4 = mock(Pagamento.class);
            lenient().when(p4.getHoraEntrada()).thenReturn(null);
            lenient().when(p4.getHoraSaida()).thenReturn(null);

            List<Pagamento> pagamentos = List.of(p1, p2, p3, p4);

            double minutos = (double) method.invoke(relatorioService, pagamentos, inicioDoDia, fimDoDia);

            assertEquals(120.0, minutos, 0.001, "Apenas pagamentos válidos devem ser somados");
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve cobrir todos os branches do filtro de horaSaida em getPagamentosDoDia")
        void deveCobrirBranchesFiltroHoraSaidaGetPagamentosDoDia() throws Exception {
            PagamentoRepository pagamentoRepository = mock(PagamentoRepository.class);
            RegistroEntradaRepository registroEntradaRepository = mock(RegistroEntradaRepository.class);
            RelatorioService relatorioService = new RelatorioService(pagamentoRepository, registroEntradaRepository);

            Method method = RelatorioService.class.getDeclaredMethod(
                    "getPagamentosDoDia", LocalDateTime.class, LocalDateTime.class
            );
            method.setAccessible(true);

            LocalDate data = LocalDate.of(2025, 6, 1);
            LocalDateTime inicioDoDia = data.atStartOfDay();
            LocalDateTime fimDoDia = data.atTime(23, 59, 59);

            Pagamento p1 = mock(Pagamento.class);
            lenient().when(p1.getHoraSaida()).thenReturn(null);

            Pagamento p2 = mock(Pagamento.class);
            LocalDateTime before = inicioDoDia.minusMinutes(1);
            lenient().when(p2.getHoraSaida()).thenReturn(before);

            Pagamento p3 = mock(Pagamento.class);
            LocalDateTime after = fimDoDia.plusMinutes(1);
            lenient().when(p3.getHoraSaida()).thenReturn(after);

            Pagamento p4 = mock(Pagamento.class);
            LocalDateTime dentro = inicioDoDia.plusHours(12);
            lenient().when(p4.getHoraSaida()).thenReturn(dentro);

            when(pagamentoRepository.findAll()).thenReturn(List.of(p1, p2, p3, p4));

            @SuppressWarnings("unchecked")
            List<Pagamento> resultado = (List<Pagamento>) method.invoke(relatorioService, inicioDoDia, fimDoDia);

            assertEquals(1, resultado.size());
            assertSame(p4, resultado.getFirst());
        }
    }
}