package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private LocalDateTime entradaValida;
    private Veiculo veiculoValido;

    @BeforeEach
    void setup() {
        entradaValida = LocalDateTime.now().plusHours(1);
        veiculoValido = criarVeiculo(entradaValida);
    }

    private Veiculo criarVeiculo(LocalDateTime horaEntrada) {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setTipoVeiculo("carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(horaEntrada);
        return veiculo;
    }

    @Nested
    @DisplayName("Testes de Cadastro de Veículo")
    class TestesDeCadastroVeiculo {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Salvar veículo com dados válidos deve ter sucesso")
        void salvarVeiculo_comDadosValidos_deveTerSucesso() {
            when(veiculoRepository.findByPlaca(veiculoValido.getPlaca()))
                    .thenReturn(Optional.empty());
            when(veiculoRepository.save(any(Veiculo.class)))
                    .thenReturn(veiculoValido);

            Veiculo result = veiculoService.cadastrarVeiculo(
                    veiculoValido.getPlaca(),
                    veiculoValido.getHoraEntrada(),
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
        @DisplayName("Salvar veículo sem placa válida deve lançar exceção")
        void salvarVeiculo_quandoPlacaVazia_deveLancarIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> veiculoService.cadastrarVeiculo("", entradaValida, "carro", "Fusca", "azul")
            );

            assertEquals("Placa não pode ser vazia", ex.getMessage());
            verify(veiculoRepository, never()).save(any());
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Salvar veículo com hora de entrada passada deve lançar exceção")
        void salvarVeiculo_quandoHoraEntradaPassada_deveLancarIllegalArgumentException() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> veiculoService.cadastrarVeiculo(
                            "ABC1234",
                            LocalDateTime.now().minusDays(1),
                            "carro",
                            "Fusca",
                            "azul"
                    )
            );

            assertEquals("Hora de entrada não pode ser nula", ex.getMessage());
            verify(veiculoRepository, never()).save(any());
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Salvar veículo com placa duplicada deve lançar exceção")
        void salvarVeiculo_quandoPlacaDuplicada_deveLancarIllegalArgumentException() {
            when(veiculoRepository.findByPlaca(veiculoValido.getPlaca()))
                    .thenReturn(Optional.of(veiculoValido));

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> veiculoService.cadastrarVeiculo(
                            veiculoValido.getPlaca(),
                            entradaValida,
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
    @DisplayName("Testes de Atualização de Veículo")
    class TestesDeAtualizacaoVeiculo {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Atualizar veículo existente deve retornar veículo atualizado")
        void atualizarVeiculo_quandoExistente_deveRetornarVeiculoAtualizado() {
            when(veiculoRepository.findById(1L))
                    .thenReturn(Optional.of(veiculoValido));
            when(veiculoRepository.save(veiculoValido))
                    .thenReturn(veiculoValido);

            Veiculo updated = veiculoService.atualizarVeiculo(
                    1L,
                    "ABC1234",
                    "carro",
                    "Fusca",
                    "azul"
            );

            assertSame(veiculoValido, updated);
            verify(veiculoRepository).save(veiculoValido);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Atualizar veículo inexistente deve lançar exceção")
        void atualizarVeiculo_quandoInexistente_deveLancarResponseStatusException() {
            when(veiculoRepository.findById(2L))
                    .thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(
                    ResponseStatusException.class,
                    () -> veiculoService.atualizarVeiculo(
                            2L,
                            "ABC1234",
                            "carro",
                            "Fusca",
                            "azul"
                    )
            );

            assertEquals("Veículo não encontrado", ex.getReason());
            verify(veiculoRepository, never()).save(any());
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Atualizar veículo com placa vazia deve lançar ResponseStatusException")
        void atualizarVeiculo_quandoPlacaVazia_deveLancarResponseStatusException() {
            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    veiculoService.atualizarVeiculo(
                            1L,
                            "",
                            "carro",
                            "Fusca",
                            "azul"
                    )
            );

            assertEquals("Placa não pode ser vazia", exception.getReason());
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            verify(veiculoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Deletação de Veículo")
    class TestesDeDeletacaoVeiculo {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deletar veículo existente deve remover veículo")
        void deletarVeiculo_quandoExistente_deveRemoverVeiculo() {
            when(veiculoRepository.findById(1L))
                    .thenReturn(Optional.of(veiculoValido));

            veiculoService.deletarVeiculo(1L);

            verify(veiculoRepository).delete(veiculoValido);
        }

        @Test
        @Tag("UnitTest")
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
}
