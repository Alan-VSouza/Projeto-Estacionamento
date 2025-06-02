package br.ifsp.demo.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import br.ifsp.demo.exception.EstacionamentoLotadoException;
import br.ifsp.demo.service.CalculadoraDeTarifa;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

class EstacionamentoTest {

    private Estacionamento estacionamento;
    private Veiculo veiculo;

    @BeforeEach
    void setup() {
        estacionamento = new Estacionamento("Estacionamento Teste", "Rua Teste", 10);
        veiculo = new Veiculo("ABC-1234", "carro", "escort", "prata");
    }


    @Nested
    @DisplayName("Testes de mutante")
    class TestesDeMutante {

        @Nested
        @DisplayName("Testes do Construtor")
        class TestesDoConstrutor {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o nome for nulo")
            void deveLancarExcecaoQuandoNomeForNulo() {
                String nomeNulo = null;
                String mensagemEsperada = "Nome do estacionamento não pode ser nulo ou vazio";

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Estacionamento(nomeNulo, "Rua Válida, 123", 100);
                });

                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(strings = {"", "  ", "\t"})
            @DisplayName("Deve lançar exceção quando o endereço for vazio ou em branco")
            void deveLancarExcecaoQuandoEnderecoForVazioOuEmBranco(String enderecoInvalido) {
                String mensagemEsperada = "Endereço do estacionamento não pode ser nulo ou vazio";

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Estacionamento("Nome Válido", enderecoInvalido, 100);
                });

                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(ints = {0, -1, -100})
            @DisplayName("Deve lançar exceção quando a capacidade for zero ou negativa")
            void deveLancarExcecaoQuandoCapacidadeForInvalida(int capacidadeInvalida) {
                String mensagemEsperada = "Capacidade do estacionamento precisa ser maior que zero";

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Estacionamento("Nome Válido", "Endereço Válido", capacidadeInvalida);
                });
                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando a capacidade for exatamente zero")
            void deveLancarExcecaoQuandoCapacidadeForZero() {
                String mensagemEsperada = "Capacidade do estacionamento precisa ser maior que zero";

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Estacionamento("Nome Válido", "Endereço Válido", 0);
                });
                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }

        }

        @Nested
        @DisplayName("Testes do Método registrarEntrada")
        class TestesDoMetodoRegistrarEntrada {


            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve registrar entrada com sucesso quando há vaga")
            void deveRegistrarEntradaComSucessoQuandoHaVaga() {
                int ocupacaoAtual = 5;

                RegistroEntrada registro = assertDoesNotThrow(() ->
                        estacionamento.registrarEntrada(veiculo, ocupacaoAtual)
                );

                assertThat(registro).isNotNull();
                assertThat(registro.getVeiculo()).isEqualTo(veiculo);
            }

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(ints = {10, 11})
            @DisplayName("Deve lançar exceção quando o estacionamento estiver lotado")
            void deveLancarExcecaoQuandoEstacionamentoEstiverLotado(int ocupacaoAtual) {
                String mensagemEsperada = "O estacionamento está lotado";

                EstacionamentoLotadoException excecao = assertThrows(EstacionamentoLotadoException.class, () -> {
                    estacionamento.registrarEntrada(veiculo, ocupacaoAtual);
                });

                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }
        }

        @Nested
        @DisplayName("Testes do Método registroSaida")
        class TestesDoMetodoRegistroSaida {

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar exceção quando o RegistroEntrada for nulo")
            void deveLancarExcecaoQuandoRegistroEntradaForNulo() {

                String mensagemEsperada = "Registro de entrada não pode ser nulo";

                CalculadoraDeTarifa calculadoraMock = mock(CalculadoraDeTarifa.class);

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    estacionamento.registroSaida(null, LocalDateTime.now(), calculadoraMock);
                });

                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }

        }

        @Nested
        @DisplayName("Testes do corpo da classe")
        class TestesDoCorpoDaClasse {


            @Test
            @DisplayName("Deve retornar excecao ao setar endereco nulo")
            void deveRetornarExcecaoAoSetarEnderecoNulo() {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    estacionamento.setEndereco(null);
                });

                assertThat(excecao.getMessage()).isEqualTo("Endereço não pode ser nulo ou vazio");
            }

            @Test
            @DisplayName("Deve retornar excecao ao setar nome nulo")
            void deveRetornarExcecaoAoSetarNomeNulo() {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    estacionamento.setNome(null);
                });

                assertThat(excecao.getMessage()).isEqualTo("Nome não pode ser nulo ou vazio");
            }

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(ints = {0, -1})
            @DisplayName("Deve lançar exceção ao tentar alterar capacidade para valor invalido")
            void deveLancarExcecaoAoAlterarCapacidadeParaValorInvalido(int valor) {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    estacionamento.setCapacidade(valor);
                });

                assertThat(excecao.getMessage()).isEqualTo("Capacidade deve ser maior que zero.");
            }

        }
    }
}