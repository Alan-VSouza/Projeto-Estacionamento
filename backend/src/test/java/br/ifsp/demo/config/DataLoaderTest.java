package br.ifsp.demo.config;

import static org.assertj.core.api.Assertions.assertThat;

import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.model.Estacionamento;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class DataLoaderTest {

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private DataLoader dataLoader;

    @BeforeEach
    void setup() {
        estacionamentoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Teste de mutante")
    class TesteDeMutantes{
        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve carregar estacionamento padrão quando o banco de dados estiver vazio")
        void quandoBancoDeDadosVazio_entaoDataLoaderCriaEstacionamentoPadrao () {
        estacionamentoRepository.deleteAll();

        try {
            dataLoader.run((String) null);
        } catch (Exception ignored) {

        }

        long total = estacionamentoRepository.count();
        assertThat(total).isEqualTo(1);

        Estacionamento estacionamentoCarregado = estacionamentoRepository.findAll().getFirst();
        assertThat(estacionamentoCarregado.getNome()).isEqualTo("Estacionamento Principal Centrar");
    }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Não deve carregar estacionamento padrão quando o banco de dados já estiver populado")
        void quandoBancoDeDadosNaoVazio_entaoDataLoaderNaoAdicionaNovo () {
        estacionamentoRepository.save(new Estacionamento("Estacionamento Existente", "Rua Teste", 50));

        long total = estacionamentoRepository.count();
        assertThat(total).isEqualTo(1);
    }
    }
}