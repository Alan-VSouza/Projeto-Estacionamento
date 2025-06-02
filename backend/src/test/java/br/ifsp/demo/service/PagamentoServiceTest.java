package br.ifsp.demo.service;

import br.ifsp.demo.components.ValorPermanencia;
import br.ifsp.demo.exception.PagamentoNaoEncontradoException;
import br.ifsp.demo.exception.VeiculoNaoEncontradoException;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.components.CalculadoraTempoPermanencia;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.PagamentoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Nested;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private VeiculoService veiculoService;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Veiculo veiculo;
    private Pagamento pagamento;
    private RegistroEntrada registroEntrada;

    @BeforeEach
    void setUp () {
        veiculo = new Veiculo("BQF-2994",
                "carro",
                "Escort",
                "prata");

        registroEntrada = new RegistroEntrada(veiculo, 1);

        LocalDateTime entrada = LocalDateTime.now();
        LocalDateTime saida = entrada.plusHours(3);
        CalculadoraDeTarifa calculadoraDeTarifa = new CalculadoraTempoPermanencia(new ValorPermanencia());
        double valorCaculado = calculadoraDeTarifa.calcularValor(entrada, saida);

        RegistroEntrada mockRegistroEntrada = mock(RegistroEntrada.class);
        when(mockRegistroEntrada.getVeiculo()).thenReturn(veiculo);
        when(mockRegistroEntrada.getHoraEntrada()).thenReturn(entrada);

        pagamento = new Pagamento(mockRegistroEntrada, saida, calculadoraDeTarifa);
    }


    @Nested
    @DisplayName("TDD Tests")
    class TddTests {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve salvar o pagamento")
        void deveSalvarPagamento() {

            pagamentoService.salvarPagamento(pagamento);

            verify(pagamentoRepository, times(1)).save(pagamento);
            verify(veiculoService, never()).deletarVeiculo(any());

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve deletar o pagamento")
        void deveDeletarPagamento() {

            UUID uuidParaTeste = UUID.randomUUID();

            when(pagamentoRepository.existsById(uuidParaTeste)).thenReturn(true);

            pagamentoService.deletarPagamento(uuidParaTeste);

            verify(pagamentoRepository, times(1)).existsById(uuidParaTeste);
            verify(pagamentoRepository, times(1)).deleteById(uuidParaTeste);


        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve lançar exceção ao tentar deletar pagamento com UUID inexistente")
        void deletarPagamentoPorUuid_quandoPagamentoNaoExiste_lancaExcecao() {
            UUID uuidInexistente = UUID.randomUUID();

            when(pagamentoRepository.existsById(uuidInexistente)).thenReturn(false);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    pagamentoService.deletarPagamento(uuidInexistente)
            );

            assertEquals("Pagamento com UUID " + uuidInexistente + " não encontrado.", exception.getMessage());
            verify(pagamentoRepository, times(1)).existsById(uuidInexistente);
            verify(pagamentoRepository, never()).deleteById(any(UUID.class));
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve encontrar o pagamento pelo UUID")
        void deveEncontrarPagamentoPeloUuid() {
            UUID uuidParaTeste = UUID.randomUUID();

            when(pagamentoRepository.findById(uuidParaTeste)).thenReturn(Optional.of(pagamento));

            Pagamento resultado = pagamentoService.buscarPorId(uuidParaTeste);

            assertNotNull(resultado);
            assertEquals(pagamento, resultado);
            assertEquals(pagamento.getPlaca(), resultado.getPlaca());

            verify(pagamentoRepository, times(1)).findById(uuidParaTeste);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve buscar pagamento por data")
        void deveBuscarPagamentoPorData() {
            LocalDate dataParaTeste = LocalDate.of(2025, 5, 2);
            LocalDateTime inicioDoDia = dataParaTeste.atStartOfDay();
            LocalDateTime fimDoDia = dataParaTeste.atTime(23, 59, 59);

            RegistroEntrada mockRegistroParaPagamento = mock(RegistroEntrada.class);
            when(mockRegistroParaPagamento.getVeiculo()).thenReturn(veiculo);

            when(mockRegistroParaPagamento.getHoraEntrada()).thenReturn(dataParaTeste.atTime(8, 0));

            CalculadoraDeTarifa calculadora = new CalculadoraTempoPermanencia(new ValorPermanencia());

            Pagamento pagamentoNaData = new Pagamento(
                    mockRegistroParaPagamento,
                    dataParaTeste.atTime(10, 30, 0),
                    calculadora
            );

            List<Pagamento> listaEsperadaDoRepositorio = List.of(pagamentoNaData);

            when(pagamentoRepository.findByHoraSaidaBetween(inicioDoDia, fimDoDia))
                    .thenReturn(listaEsperadaDoRepositorio);

            List<Pagamento> resultado = pagamentoService.buscarPorData(dataParaTeste);

            assertThat(resultado).isNotNull();
            assertThat(resultado).hasSize(1);
            assertThat(resultado).containsExactly(pagamentoNaData);

            verify(pagamentoRepository, times(1)).findByHoraSaidaBetween(inicioDoDia, fimDoDia);

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar lista vazia ao buscar por data sem pagamentos")
        void buscarPorData_quandoNaoExistemPagamentosNaData_retornaListaVazia() {
            LocalDate dataSemPagamentos = LocalDate.of(2025, 5, 3);
            LocalDateTime inicioDoDia = dataSemPagamentos.atStartOfDay();
            LocalDateTime fimDoDia = dataSemPagamentos.atTime(23, 59, 59);

            when(pagamentoRepository.findByHoraSaidaBetween(inicioDoDia, fimDoDia))
                    .thenReturn(Collections.emptyList());

            List<Pagamento> resultado = pagamentoService.buscarPorData(dataSemPagamentos);

            assertThat(resultado).isNotNull();
            assertThat(resultado).isEmpty();
            verify(pagamentoRepository, times(1)).findByHoraSaidaBetween(inicioDoDia, fimDoDia);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar o total arrecadado quando existem pagamentos")
        void deveRetornarTotalArrecadadoQuandoExistemPagamentos() {
            LocalDate data = LocalDate.of(2025, 5, 3);
            LocalDateTime inicio = data.atStartOfDay();
            LocalDateTime fim = data.atTime(23, 59, 59);

            when(pagamentoRepository.somarPagamentosPorData(inicio, fim)).thenReturn(100.0);

            double total = pagamentoService.calcularTotalArrecadadoPorData(data);

            assertEquals(100.0, total);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar zero quando nao existirem pagamentos")
        void deveRetornarZeroQuandoNaoExistemPagamentos() {
            LocalDate data = LocalDate.of(2025, 5, 3);
            LocalDateTime inicio = data.atStartOfDay();
            LocalDateTime fim = data.atTime(23, 59, 59);

            when(pagamentoRepository.somarPagamentosPorData(inicio, fim)).thenReturn(null);

            double total = pagamentoService.calcularTotalArrecadadoPorData(data);

            assertEquals(0.0, total);
        }

    }

    @Nested
    @DisplayName("Testando mensagens de erro")
    class TestandoMensagensDeErro {


        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve lançar IllegalArgumentException ao tentar salvar um Pagamento nulo")
        void salvarPagamento_quandoPagamentoEhNulo_lancaExcecao() {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    pagamentoService.salvarPagamento(null)
            );

            assertEquals("O objeto de pagamento não pode ser nulo.", exception.getMessage());
            verify(pagamentoRepository, never()).save(any(Pagamento.class));
        }



        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("mensgaem de erro ao tentar excluir pagamento nulo")
        void mensagemDeErroAoExcluirPagamentoNulo() {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    pagamentoService.deletarPagamento(null)
            );

            assertEquals("UUID não pode ser nulo", exception.getMessage());

            verify(pagamentoRepository, never()).existsById(any());
            verify(pagamentoRepository, never()).deleteById(any());

        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Functional")
        @CsvSource(
                value = {
                        "null, UUID não pode ser nulo",
                        "123e4567-e89b-12d3-a456-426614174000, Pagamento com UUID 123e4567-e89b-12d3-a456-426614174000 não encontrado."
                },
                nullValues = "null"
        )
        @DisplayName("Deve lançar exceções corretas ao buscar pagamento por UUID nulo ou inexistente")
        void buscarPorId_comUuidNuloOuInexistente_lancaIllegalArgumentExceptionComMensagemCorreta(String uuidStr, String mensagemEsperada) {

            UUID uuidParaTeste = (uuidStr == null) ? null : UUID.fromString(uuidStr);

            if (uuidParaTeste != null) {
                when(pagamentoRepository.findById(uuidParaTeste)).thenReturn(Optional.empty());
            }

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    pagamentoService.buscarPorId(uuidParaTeste)
            );

            assertEquals(mensagemEsperada, exception.getMessage());

            if (uuidParaTeste != null) {
                verify(pagamentoRepository, times(1)).findById(uuidParaTeste);
            } else {
                verify(pagamentoRepository, never()).findById(any());
            }
        }
    }
}