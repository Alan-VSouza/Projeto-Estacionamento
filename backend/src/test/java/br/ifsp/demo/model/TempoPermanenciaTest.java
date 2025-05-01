package br.ifsp.demo.model;

import br.ifsp.demo.service.ValorPermanencia;
import org.junit.jupiter.api.*;

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



}