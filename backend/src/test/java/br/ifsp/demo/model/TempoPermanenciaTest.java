package br.ifsp.demo.model;

import br.ifsp.demo.service.ValorPermanencia;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class TempoPermanenciaTest {

    private TempoPermanencia tempoPermanencia;
    private ValorPermanencia valorPermanencia;

    @BeforeEach
    void setUp() {

        valorPermanencia = new ValorPermanencia();
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

    }

}