package br.ifsp.demo.components;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CalculadoraTempoPermanenciaTest {

    private CalculadoraTempoPermanencia calculadoraTempoPermanencia;
    private LocalDateTime entrada;
    private LocalDateTime saida;

    @BeforeEach
    void setUp() {
        entrada = LocalDateTime.now();

        ValorPermanencia valorPermanencia = new ValorPermanencia();
        calculadoraTempoPermanencia = new CalculadoraTempoPermanencia(valorPermanencia);
    }

    @Nested
    @DisplayName("TDD testes")
    class TDDTestes {

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor de uma hora de permanencia")
        void calcularOValorDeUmaHoraDePermanencia() {
            LocalDateTime saida = entrada.plusMinutes(50);

            assertEquals(10.0, calculadoraTempoPermanencia.calcularValor(entrada, saida));

        }

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor por hora de permanencia em permanencia menor que seis horas")
        void calcularOValorPorHoraDePermanenciaEmPermanenciaMenorQueSeisHoras() {

            saida = entrada.plusHours(5);

            assertEquals(35.0, calculadoraTempoPermanencia.calcularValor(entrada, saida));

        }

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor a partir de seis hora de permanencia")
        void calcularOValorAPartirDeSeisHoraDePermanencia() {

            saida = entrada.plusHours(8);

            assertEquals(51.0, calculadoraTempoPermanencia.calcularValor(entrada, saida));

        }

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor a partir de doze horas de permanencia")
        void calcularOValorAPartirDeDozeHorasDePermanencia() {

            saida = entrada.plusHours(19);

            assertEquals(111.0, calculadoraTempoPermanencia.calcularValor(entrada, saida));

        }

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor a partir de vinte e quatro horas de permanencia")
        void calcularOValorAPartirDeVinteQuatroHorasDePermanencia() {

            saida = entrada.plusHours(29);

            assertEquals(160.0, calculadoraTempoPermanencia.calcularValor(entrada, saida));

        }
    }

    @Nested
    @DisplayName("Testando funcionamento da classe completa")
    class TestandoFuncionamentoDaClassCompleta {

        @ParameterizedTest
        @Tag("Functional")
        @Tag("UnitTest")
        @CsvSource({
                "1, 10.0",
                "4, 34.0",
                "5, 35.0",
                "6, 35.0",
                "8, 51.0",
                "9, 55.0",
                "12, 55.0",
                "20, 119.0",
                "24, 120.0"
        })
        @DisplayName("Testando valores limites")
        void testandoValoresLimites(int horas, double custo) {

            saida = entrada.plusHours(horas);

            assertEquals(custo, calculadoraTempoPermanencia.calcularValor(entrada, saida));

        }

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @DisplayName("Testando valor de hora invalido")
        void testandoValorDeHoraInvalido() {

            saida = entrada.minusHours(1);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                calculadoraTempoPermanencia.calcularValor(entrada, saida);
            });

            assertEquals("Horário de saída não pode ser antes do horário de entrada", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Structural Tests")
    class StructuralTests {

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Calcular permanência com entrada e saída iguais deve considerar 1 hora mínima")
        void calcularPermanenciaComDuracaoZero() {
            saida = entrada;

            double resultado = calculadoraTempoPermanencia.calcularValor(entrada, saida);

            assertEquals(10.0, resultado);
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção quando entrada for nula")
        void deveLancarExcecaoQuandoEntradaForNula() {
            LocalDateTime saida = LocalDateTime.now().plusHours(1);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                calculadoraTempoPermanencia.calcularValor(null, saida);
            });

            assertEquals("horas não podem ser nulas", ex.getMessage());
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção quando saída for nula")
        void deveLancarExcecaoQuandoSaidaForNula() {
            LocalDateTime entrada = LocalDateTime.now();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                calculadoraTempoPermanencia.calcularValor(entrada, null);
            });

            assertEquals("horas não podem ser nulas", ex.getMessage());
        }

    }

    @Nested
    @DisplayName("Testes de Mutante")
    class TestesDeMutante {

        @Test
        @DisplayName("Deve cobrar o valor limite de 6 horas para permanência de exatas 6 horas")
        void deveCalcularValorParaExatasSeisHoras() {
            LocalDateTime saida = entrada.plusHours(5).plusMinutes(1);

            double valorEsperado = 35.0;

            double valorCalculado = calculadoraTempoPermanencia.calcularValor(entrada, saida);

            assertEquals(valorEsperado, valorCalculado);
        }

        @Test
        @DisplayName("Deve cobrar o valor limite de 12 horas para permanência de exatas 12 horas")
        void deveCalcularValorParaExatasDozeHoras() {
            LocalDateTime saida = entrada.plusHours(11).plusMinutes(1);

            double valorEsperado = 55.0;

            double valorCalculado = calculadoraTempoPermanencia.calcularValor(entrada, saida);

            assertEquals(valorEsperado, valorCalculado);
        }

        @Test
        @DisplayName("Deve cobrar o valor limite de 24 horas para permanência de exatas 24 horas")
        void deveCalcularValorParaExatasVinteEQuatroHoras() {
            LocalDateTime saida = entrada.plusHours(23).plusMinutes(1);

            double valorEsperado = 120.0;

            double valorCalculado = calculadoraTempoPermanencia.calcularValor(entrada, saida);

            assertEquals(valorEsperado, valorCalculado);


        }
    }

}