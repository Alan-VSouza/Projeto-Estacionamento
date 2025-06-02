package br.ifsp.demo.model;

import br.ifsp.demo.service.CalculadoraDeTarifa;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PagamentoTest {

    private CalculadoraDeTarifa calculadoraDeTarifa;
    private LocalDateTime saida;
    private LocalDateTime entrada;
    private RegistroEntrada registroEntrada;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo("123456", "carro", "escort", "prata");
        entrada = LocalDateTime.now();
        saida = entrada.plusHours(1);
        registroEntrada = new RegistroEntrada(veiculo, 1);

        calculadoraDeTarifa = mock(CalculadoraDeTarifa.class);
        when(calculadoraDeTarifa.calcularValor(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(25.0);
    }

    @Nested
    @DisplayName("Teste de Mutante")
    class TesteDeMutante {

        @Nested
        @DisplayName("Testes do Construtor")
        class TestesDoConstrutor {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o registro de entrada for nulo")
            void deveLancarUmaExcecaoQuandoORegistroDeEntradaForNulo() {

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    Pagamento pagamento = new Pagamento(null, LocalDateTime.now(), calculadoraDeTarifa);
                });

                assertThat(exception.getMessage()).isEqualTo("Registro de entrada não pode ser nulo");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o hora de saida for nulo")
            void deveLancarUmaExcecaoQuandoORHoraDeSaidaForNulo() {

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    Pagamento pagamento = new Pagamento(registroEntrada, null, calculadoraDeTarifa);
                });

                assertThat(exception.getMessage()).isEqualTo("Hora de saída não pode ser nula");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o tarifa for nulo")
            void deveLancarUmaExcecaoQuandoORTarifaForNulo() {

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    Pagamento pagamento = new Pagamento(registroEntrada, saida, null);
                });

                assertThat(exception.getMessage()).isEqualTo("Tarifa não pode ser nula");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando hora de saída for anterior à de entrada")
            void deveLancarExcecaoParaSaidaInvalida() {
                LocalDateTime saidaInvalida = entrada.minusHours(1);

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Pagamento(registroEntrada, saidaInvalida, calculadoraDeTarifa);
                });

                assertThat(excecao.getMessage()).isEqualTo("Hora de saída não pode ser antes da hora de entrada");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o valor da tarifa for negativo")
            void deveLancarExcecaoQuandoValorForNegativo() {
                String mensagemEsperada = "Valor da tarifa não pode ser negativo";
                when(calculadoraDeTarifa.calcularValor(any(LocalDateTime.class), any(LocalDateTime.class)))
                        .thenReturn(-10.0);

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Pagamento(registroEntrada, saida, calculadoraDeTarifa);
                });

                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve registrar saída com sucesso quando a tarifa calculada for zero")
            void deveRegistrarSaidaComSucessoQuandoTarifaForZero() {
                Estacionamento estacionamento = new Estacionamento("Nome", "Endereco", 10);
                LocalDateTime horaSaida = registroEntrada.getHoraEntrada().plusHours(2);


                when(calculadoraDeTarifa.calcularValor(any(LocalDateTime.class), any(LocalDateTime.class)))
                        .thenReturn(0.0);

                assertDoesNotThrow(() -> {
                    estacionamento.registroSaida(registroEntrada, horaSaida, calculadoraDeTarifa);
                });
            }

        }

        @Nested
        @DisplayName("Teste do segundo construtor")
        class TesteDoSegundoConstrutor {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o registro de entrada for nulo")
            void deveLancarUmaExcecaoQuandoORegistroDeEntradaForNulo() {

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    Pagamento pagamento = new Pagamento(null, entrada, saida, calculadoraDeTarifa);
                });

                assertThat(exception.getMessage()).isEqualTo("Registro de entrada não pode ser nulo");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o hora de entrada for nulo")
            void deveLancarUmaExcecaoQuandoORHoraDeSaidaForNulo() {

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    Pagamento pagamento = new Pagamento(registroEntrada, null,saida, calculadoraDeTarifa);
                });

                assertThat(exception.getMessage()).isEqualTo("Hora de entrada não pode ser nula");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o hora de saida for nulo")
            void deveLancarUmaExcecaoQuandoORHoraDeEntradaForNulo() {

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    Pagamento pagamento = new Pagamento(registroEntrada, entrada,null, calculadoraDeTarifa);
                });

                assertThat(exception.getMessage()).isEqualTo("Hora de saída não pode ser nula");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o tarifa for nulo")
            void deveLancarUmaExcecaoQuandoORTarifaForNulo() {

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                    Pagamento pagamento = new Pagamento(registroEntrada, entrada, saida, null);
                });

                assertThat(exception.getMessage()).isEqualTo("Tarifa não pode ser nula");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando hora de saída for anterior à de entrada")
            void deveLancarExcecaoParaSaidaInvalida() {
                LocalDateTime saidaInvalida = entrada.minusHours(1);

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Pagamento(registroEntrada, entrada, saidaInvalida, calculadoraDeTarifa);
                });

                assertThat(excecao.getMessage()).isEqualTo("Hora de saída não pode ser antes da hora de entrada");
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o valor da tarifa for negativo")
            void deveLancarExcecaoQuandoValorForNegativo() {
                String mensagemEsperada = "Valor da tarifa não pode ser negativo";
                when(calculadoraDeTarifa.calcularValor(any(LocalDateTime.class), any(LocalDateTime.class)))
                        .thenReturn(-10.0);

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Pagamento(registroEntrada, entrada, saida, calculadoraDeTarifa);
                });

                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }

        }

    }
}