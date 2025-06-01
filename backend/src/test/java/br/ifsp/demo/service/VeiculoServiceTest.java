package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private Veiculo veiculoValido;

    @BeforeEach
    void setup() {
        veiculoValido = new Veiculo("ABC6969", "carro", "Fusca", "azul");
    }

    @Nested
    @DisplayName("Testes de Cadastro de Veículo")
    class TestesDeCadastroVeiculo {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Salvar veículo com dados válidos deve ter sucesso")
        void salvarVeiculo_comDadosValidos_deveTerSucesso() {
            when(veiculoRepository.findByPlaca(veiculoValido.getPlaca()))
                    .thenReturn(Optional.empty());
            when(veiculoRepository.save(any(Veiculo.class)))
                    .thenReturn(veiculoValido);

            Veiculo result = veiculoService.cadastrarVeiculo(
                    veiculoValido.getPlaca(),
                    veiculoValido.getTipoVeiculo(),
                    veiculoValido.getModelo(),
                    veiculoValido.getCor()
            );

            assertSame(veiculoValido, result);
            verify(veiculoRepository).save(any(Veiculo.class));
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Salvar veículo sem placa válida deve lançar exceção")
        void salvarVeiculo_quandoPlacaVazia_deveLancarIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> veiculoService.cadastrarVeiculo("", "carro", "Fusca", "azul")
            );

            assertEquals("Placa não pode ser vazia", ex.getMessage());
            verify(veiculoRepository, never()).save(any());
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Salvar veículo com placa duplicada deve lançar exceção")
        void salvarVeiculo_quandoPlacaDuplicada_deveLancarIllegalArgumentException() {
            when(veiculoRepository.findByPlaca(veiculoValido.getPlaca()))
                    .thenReturn(Optional.of(veiculoValido));

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> veiculoService.cadastrarVeiculo(
                            veiculoValido.getPlaca(),
                            "carro",
                            "Fusca",
                            "azul"
                    )
            );

            assertEquals("Placa já cadastrada", ex.getMessage());
            verify(veiculoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Deletação de Veículo")
    class TestesDeDeletacaoVeiculo {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deletar veículo existente deve remover veículo")
        void deletarVeiculo_quandoExistente_deveRemoverVeiculo() {
            when(veiculoRepository.findById(1L))
                    .thenReturn(Optional.of(veiculoValido));

            veiculoService.deletarVeiculo(1L);

            verify(veiculoRepository).delete(veiculoValido);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deletar veículo inexistente deve lançar exceção")
        void deletarVeiculo_quandoInexistente_deveLancarIllegalArgumentException() {
            when(veiculoRepository.findById(3L))
                    .thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> veiculoService.deletarVeiculo(3L)
            );

            assertEquals("Veículo não encontrado", ex.getMessage());
            verify(veiculoRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Structural Tests")
    class StructuralTests {
        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Salvar veículo com placa nula deve lançar exceção")
        void salvarVeiculo_quandoPlacaNula_deveLancarIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> veiculoService.cadastrarVeiculo(null, "carro", "Fusca", "azul")
            );

            assertEquals("Placa não pode ser vazia", ex.getMessage());
            verify(veiculoRepository, never()).save(any());
        }
    }
}