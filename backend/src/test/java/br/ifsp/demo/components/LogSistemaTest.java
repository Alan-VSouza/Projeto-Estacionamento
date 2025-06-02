package br.ifsp.demo.components;

import br.ifsp.demo.components.LogSistema;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class LogSistemaTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    @Tag("UnitTest")
    @Tag("Mutation")
    @DisplayName("Deve registrar mensagem de cancelamento correto no log")
    void deveRegistrarMensagemDeCancelamentoCorretaNoLog() {
        LogSistema logSistema = new LogSistema();
        String placa = "BRA2E19";
        String motivo = "Vaga incorreta";
        String mensagemEsperada = "Cancelamento do check-in do veículo " + placa + " realizado. Motivo: " + motivo;

        logSistema.registrarCancelamento(placa, motivo);


        assertThat(outputStreamCaptor.toString().trim()).isEqualTo(mensagemEsperada);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }


}