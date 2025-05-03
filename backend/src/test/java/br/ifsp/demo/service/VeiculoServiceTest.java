package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        veiculoValido = new Veiculo();
        veiculoValido.setPlaca("ABC1234");
        veiculoValido.setTipoVeiculo("carro");
        veiculoValido.setModelo("Fusca");
        veiculoValido.setCor("azul");
        veiculoValido.setHoraEntrada(entradaValida);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve salvar o veículo corretamente")
    void deveSalvarVeiculoComSucesso() {
        when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.empty());
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculoValido);

        Veiculo result = veiculoService.cadastrarVeiculo(
                veiculoValido.getPlaca(),
                veiculoValido.getHoraEntrada(),
                veiculoValido.getTipoVeiculo(),
                veiculoValido.getModelo(),
                veiculoValido.getCor()
        );

        assertEquals(veiculoValido.getPlaca(), result.getPlaca());
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Não deve salvar sem placa válida")
    void naoDeveSalvarSemPlacaValida() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> veiculoService.cadastrarVeiculo("", entradaValida, "carro", "Fusca", "azul")
        );
        assertEquals("Placa não pode ser vazia", ex.getMessage());
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve salvar com hora de entrada passada")
    void naoDeveSalvarComHoraEntradaPassada() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> veiculoService.cadastrarVeiculo("ABC1234", LocalDateTime.now().minusDays(1), "carro", "Fusca", "azul")
        );
        assertTrue(ex.getMessage().contains("Hora de entrada não pode ser nula"));
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve salvar placa duplicada")
    void naoDeveSalvarPlacaDuplicada() {
        when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculoValido));
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> veiculoService.cadastrarVeiculo(
                        veiculoValido.getPlaca(), entradaValida, "carro", "Fusca", "azul"
                )
        );
        assertEquals("Placa já cadastrada", ex.getMessage());
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar veículo existente")
    void deveAtualizarVeiculoComSucesso() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoValido));
        when(veiculoRepository.save(any())).thenReturn(veiculoValido);

        Veiculo updated = veiculoService.atualizarVeiculo(
                1L, "ABC1234", "carro", "Fusca", "azul"
        );

        assertEquals("ABC1234", updated.getPlaca());
        verify(veiculoRepository).save(updated);
    }

    @Test
    @DisplayName("Não deve atualizar veículo inexistente")
    void naoDeveAtualizarVeiculoInexistente() {
        when(veiculoRepository.findById(2L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> veiculoService.atualizarVeiculo(2L, "ABC1234", "carro", "Fusca", "azul")
        );
        assertEquals("Veículo não encontrado", ex.getReason());
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar veículo existente")
    void deveDeletarVeiculoComSucesso() {
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoValido));
        veiculoService.deletarVeiculo(1L);
        verify(veiculoRepository).delete(veiculoValido);
    }

    @Test
    @DisplayName("Não deve deletar veículo inexistente")
    void naoDeveDeletarVeiculoInexistente() {
        when(veiculoRepository.findById(3L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> veiculoService.deletarVeiculo(3L)
        );
        assertEquals("Veículo não encontrado", ex.getMessage());
        verify(veiculoRepository, never()).delete(any());
    }
}
