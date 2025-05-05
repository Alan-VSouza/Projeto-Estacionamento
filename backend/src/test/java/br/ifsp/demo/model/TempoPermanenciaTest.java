package br.ifsp.demo.model;

import br.ifsp.demo.components.TempoPermanencia;
import br.ifsp.demo.components.ValorPermanencia;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TempoPermanenciaTest {

    private TempoPermanencia tempoPermanencia;

    @BeforeEach
    void setUp() {

        ValorPermanencia valorPermanencia = new ValorPermanencia();
        tempoPermanencia = new TempoPermanencia(valorPermanencia);

    }

    @Nested
    @DisplayName("TDD testes")
    class TDDTestes {

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor de uma hora de permanencia")
        void calcularOValorDeUmaHoraDePermanencia() {

            assertEquals(10.0, tempoPermanencia.calcularValorDaPermanencia(1));

        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor por hora de permanencia em permanencia menor que seis horas")
        void calcularOValorPorHoraDePermanenciaEmPermanenciaMenorQueSeisHoras() {

            assertEquals(35.0, tempoPermanencia.calcularValorDaPermanencia(5));

        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor a partir de seis hora de permanencia")
        void calcularOValorAPartirDeSeisHoraDePermanencia() {

            assertEquals(51.0, tempoPermanencia.calcularValorDaPermanencia(8));

        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor a partir de doze horas de permanencia")
        void calcularOValorAPartirDeDozeHorasDePermanencia() {

            assertEquals(111.0, tempoPermanencia.calcularValorDaPermanencia(19));

        }

        @Test
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Calcular o valor a partir de vinte e quatro horas de permanencia")
        void calcularOValorAPartirDeVinteQuatroHorasDePermanencia() {

            assertEquals(160.0, tempoPermanencia.calcularValorDaPermanencia(29));

        }


    }

    @Nested
    @DisplayName("Testando funcionamento da classe completa")
    class TestandoFuncionamentoDaClassCompleta {

        @ParameterizedTest
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

            assertEquals(custo, tempoPermanencia.calcularValorDaPermanencia(horas));

        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Testando valor de hora invalido")
        void testandoValorDeHoraInvalido() {

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                tempoPermanencia.calcularValorDaPermanencia(-1);
            });

            assertEquals("Horas deve ser maior que zero", ex.getMessage());

        }

    }

}