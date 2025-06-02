package br.ifsp.demo.config;

import static org.assertj.core.api.Assertions.assertThat;

import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.model.Estacionamento;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@SpringBootTest
class DataLoaderTest {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private DataLoader dataLoader;

    @Nested
    @DisplayName("Teste de mutante")
    class TesteDeMutantes {

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve carregar estacionamento padrão e logar quando o banco estiver vazio")
        void quandoBancoDeDadosVazio_entaoDataLoaderCriaEstacionamentoPadrao() {

            estacionamentoRepository.deleteAll();
            String mensagemEsperada = ">>>> DataLoader: Estacionamento padrão 'Estacionamento Principal Central' foi criado!";

            ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
            PrintStream standardOut = System.out;
            System.setOut(new PrintStream(outputStreamCaptor));

            try {
                dataLoader.run(null);
            } catch (Exception ignored) {
            } finally {
                System.setOut(standardOut);
            }

            long total = estacionamentoRepository.count();
            assertThat(total).isEqualTo(1);

            Estacionamento estacionamentoCarregado = estacionamentoRepository.findAll().getFirst();
            assertThat(estacionamentoCarregado.getNome()).isEqualTo("Estacionamento Principal Centrar");
            assertThat(outputStreamCaptor.toString()).contains(mensagemEsperada);
        }


    }
}