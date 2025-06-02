package br.ifsp.demo.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RegistroEntradaTest {

    private Veiculo veiculo;
    RegistroEntrada registroEntrada;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo("123456", "carro", "escort", "prata");
        registroEntrada = new RegistroEntrada(veiculo);
    }

    @Nested
    @DisplayName("Teste de Mutante")
    class TesteDeMutante {

        @Nested
        @DisplayName("Testes do construtor")
        class TestesDoConstrutor {

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(ints = { 0, 201})
            @DisplayName("Deve lançar excecao quando ID da vaga for invalido")
            void deveLancarExcecaoQuandoIdDaVagaForInvalido(int id) {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    RegistroEntrada registroEntrada = new RegistroEntrada(veiculo, id);
                });

                assertThat(excecao.getMessage()).isEqualTo("ID da vaga deve estar entre 1 e 200");

            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar excecao quando id da vaga for nula")
            void deveLancarExcecaoQuandoIdDaVagaForNulo() {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    RegistroEntrada registroEntrada = new RegistroEntrada(veiculo, null);
                });

                assertThat(excecao.getMessage()).isEqualTo("ID da vaga deve estar entre 1 e 200");

            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar excecao quando veiculo for nulo")
            void deveLancarExcecaoQuandoVeiculoForNulo() {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    RegistroEntrada registroEntrada = new RegistroEntrada(null, 1);
                });

                assertThat(excecao.getMessage()).isEqualTo("Veículo não pode ser nulo");

            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve lançar excecao quando veiculo for nulo - construtor2")
            void deveLancarExcecaoQuandoVeiculoForNulo_construtor2() {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    RegistroEntrada registroEntrada = new RegistroEntrada(null);
                });

                assertThat(excecao.getMessage()).isEqualTo("Veículo não pode ser nulo");

            }

            @Test
            @DisplayName("NÃO deve lançar exceção quando vagaId for 1")
            void naoDeveLancarExcecaoQuandoVagaIdForUm() {
                assertDoesNotThrow(() -> {
                    new RegistroEntrada(veiculo, 1);
                });
            }

            @Test
            @DisplayName("NÃO deve lançar exceção quando vagaId for 200")
            void naoDeveLancarExcecaoQuandoVagaIdForDuzentos() {
                assertDoesNotThrow(() -> {
                    new RegistroEntrada(veiculo, 200);
                });
            }


        }

        @Nested
        @DisplayName("Testes para corpo da classe")
        class TestesParaCorpoDeClasse {

            @ParameterizedTest
            @Tag("UnitTest")
            @Tag("Mutation")
            @ValueSource(ints = { 0, 201})
            @DisplayName("Deve lançar excecao quando setar id invalido")
            void deveLancarExcecaoQuandoSetarIdInvalido(int id) {

                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    registroEntrada.setVagaId(id);
                });
                assertThat(excecao.getMessage()).isEqualTo("ID da vaga deve estar entre 1 e 200");
            }

            @Test
            @DisplayName("Deve lançar exceção ao tentar setar ID da vaga nulo")
            void deveLancarExcecaoAoSetarIdDaVagaNulo() {
                IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> {
                    registroEntrada.setVagaId(null);
                });
                assertThat(excecao.getMessage()).isEqualTo("ID da vaga deve estar entre 1 e 200");
            }

            @Test
            @DisplayName("NÃO deve lançar exceção quando vagaId for 1")
            void naoDeveLancarExcecaoQuandoVagaIdForUm() {
                assertDoesNotThrow(() -> {
                    registroEntrada.setVagaId(1);
                });
            }

            @Test
            @DisplayName("NÃO deve lançar exceção quando vagaId for 200")
            void naoDeveLancarExcecaoQuandoVagaIdForDuzentos() {
                assertDoesNotThrow(() -> {
                    registroEntrada.setVagaId(200);
                });
            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve setar id valido")
            void deveSetarIdValido() {

                registroEntrada.setVagaId(1);
                assertThat(registroEntrada.getVagaId()).isEqualTo(1);

            }

            @Test
            @Tag("UnitTest")
            @Tag("Mutation")
            @DisplayName("Deve retornar o id corretamente via getter")
            void deveRetornarIdCorretamenteViaGetter() throws Exception {
                RegistroEntrada registro = new RegistroEntrada(veiculo);
                UUID uuid = UUID.randomUUID();

                Field field = RegistroEntrada.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(registro, uuid);

                assertThat(registro.getId()).isEqualTo(uuid);


            }

        }

    }

}