package br.ifsp.demo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VeiculoTest {

    private static final String PLACA_VALIDA = "ABC-1234";
    private static final String TIPO_VALIDO = "Carro";
    private static final String MODELO_VALIDO = "Sedan";
    private static final String COR_VALIDA = "Preto";

    @Nested
    @DisplayName("Teste de Mutante")
    class TesteDeMutante {

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve criar Veiculo com todos os campos válidos")
        void deveCriarVeiculoComCamposValidos() {
            assertDoesNotThrow(() -> {
                new Veiculo(PLACA_VALIDA, TIPO_VALIDO, MODELO_VALIDO, COR_VALIDA);
            });
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção para placa nula ou vazia")
        @NullSource
        @ValueSource(strings = {"", "  ", "\t"})
        void deveLancarExcecaoParaPlacaInvalida(String placaInvalida) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Veiculo(placaInvalida, TIPO_VALIDO, MODELO_VALIDO, COR_VALIDA);
            });
            assertThat(exception.getMessage()).isEqualTo("Placa não pode ser nula ou vazia");
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção para tipo do veículo nulo ou vazio")
        @NullSource
        @ValueSource(strings = {"", "  ", "\t"})
        void deveLancarExcecaoParaTipoVeiculoInvalido(String tipoInvalido) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Veiculo(PLACA_VALIDA, tipoInvalido, MODELO_VALIDO, COR_VALIDA);
            });
            assertThat(exception.getMessage()).isEqualTo("Tipo do veículo não pode ser nulo ou vazio");
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção para modelo do veículo nulo ou vazio")
        @NullSource
        @ValueSource(strings = {"", "  ", "\t"})
        void deveLancarExcecaoParaModeloVeiculoInvalido(String modeloInvalido) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Veiculo(PLACA_VALIDA, TIPO_VALIDO, modeloInvalido, COR_VALIDA);
            });
            assertThat(exception.getMessage()).isEqualTo("Modelo do veículo não pode ser nulo ou vazio");
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção para cor do veículo nula ou vazia")
        @NullSource
        @ValueSource(strings = {"", "  ", "\t"})
        void deveLancarExcecaoParaCorVeiculoNulaOuVazia(String corInvalida) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Veiculo(PLACA_VALIDA, TIPO_VALIDO, MODELO_VALIDO, corInvalida);
            });
            assertThat(exception.getMessage()).isEqualTo("Cor do veículo não pode ser nula ou vazia");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção quando cor for apenas números")
        void deveLancarExcecaoParaCorApenasNumeros() {
            String corApenasNumeros = "12345";
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Veiculo(PLACA_VALIDA, TIPO_VALIDO, MODELO_VALIDO, corApenasNumeros);
            });
            assertThat(exception.getMessage()).isEqualTo("Cor não pode ser apenas números. Use nomes como: branco, preto, azul, etc.");
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção quando cor contiver caracteres especiais")
        @ValueSource(strings = {"Azul@", "Verde#", "Preto!"})
        void deveLancarExcecaoParaCorComCaracteresEspeciais(String corComEspecial) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Veiculo(PLACA_VALIDA, TIPO_VALIDO, MODELO_VALIDO, corComEspecial);
            });
            assertThat(exception.getMessage()).isEqualTo("Cor não pode conter caracteres especiais (@#!$%). Use apenas letras.");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve retornar o Id corretamente")
        void deveRetornarIdCorretamente() {
            Veiculo veiculoMock = mock(Veiculo.class);
            Long idEsperado = 123L;
            when(veiculoMock.getId()).thenReturn(idEsperado);

            Long idRetornado = veiculoMock.getId();

            assertEquals(idEsperado, idRetornado);
        }
    }

}