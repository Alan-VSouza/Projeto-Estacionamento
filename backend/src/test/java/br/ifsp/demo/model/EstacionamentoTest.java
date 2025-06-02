package br.ifsp.demo.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ifsp.demo.exception.EstacionamentoLotadoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EstacionamentoTest {

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
        }
    }
}