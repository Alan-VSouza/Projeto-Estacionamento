package br.ifsp.demo.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ifsp.demo.exception.EstacionamentoLotadoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
            @DisplayName("Deve lançar exceção quando o nome for nulo")
            void deveLancarExcecaoQuandoNomeForNulo() {
                String nomeNulo = null;
                String mensagemEsperada = "Nome do estacionamento não pode ser nulo ou vazio";

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    new Estacionamento(nomeNulo, "Rua Válida, 123", 100);
                });

                assertThat(excecao.getMessage()).isEqualTo(mensagemEsperada);
            }


        }
    }
}