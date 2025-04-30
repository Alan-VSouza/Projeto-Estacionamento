package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve salvar o veículo corretamente")
    void deveSalvarVeiculoComSucesso() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setTipoVeiculo("carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(LocalDateTime.now());

        doReturn(veiculo).when(veiculoRepository).save(any(Veiculo.class));

        Veiculo result = veiculoService.cadastrarVeiculo("ABC1234", LocalDateTime.now(), "carro", "Fusca", "azul");

        assertEquals(veiculo.getPlaca(), result.getPlaca());
        assertEquals(veiculo.getModelo(), result.getModelo());

        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Não deve salvar o veículo sem uma placa válida")
    void naoDeveSalvarVeiculoSemPlacaValida() {
        String placaInvalida = "";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.cadastrarVeiculo(placaInvalida, LocalDateTime.now(), "carro", "Fusca", "azul");
        });

        String expectedMessage = "Placa não pode ser vazia";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(veiculoRepository, times(0)).save(any(Veiculo.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Não deve salvar o veículo com modelo inválido")
    void naoDeveSalvarVeiculoComModeloInvalido() {
        String modeloInvalido = "";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.cadastrarVeiculo("XYZ9876", LocalDateTime.now(), "moto", modeloInvalido, "preto");
        });

        String expectedMessage = "Modelo não pode ser vazio";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(veiculoRepository, times(0)).save(any(Veiculo.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve atualizar os dados do veículo corretamente")
    void deveAtualizarVeiculoComSucesso() {
        Veiculo veiculoExistente = new Veiculo();
        veiculoExistente.setId(1L);
        veiculoExistente.setPlaca("ABC1234");
        veiculoExistente.setTipoVeiculo("carro");
        veiculoExistente.setModelo("Fusca");
        veiculoExistente.setCor("azul");

        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculoExistente));

        String novaPlaca = "DEF5678";
        String novoModelo = "Fusca 2.0";
        String novaCor = "verde";
        String novoTipoVeiculo = "carro";

        doReturn(veiculoExistente).when(veiculoRepository).save(any(Veiculo.class));

        Veiculo veiculoAtualizado = veiculoService.atualizarVeiculo(1L, novaPlaca, novoTipoVeiculo, novoModelo, novaCor);

        assertEquals(novaPlaca, veiculoAtualizado.getPlaca());
        assertEquals(novoModelo, veiculoAtualizado.getModelo());
        assertEquals(novaCor, veiculoAtualizado.getCor());
        assertEquals(novoTipoVeiculo, veiculoAtualizado.getTipoVeiculo());

        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Não deve atualizar um veículo que não existe")
    void naoDeveAtualizarVeiculoQueNaoExiste() {
        Long idInvalido = 999L;

        when(veiculoRepository.findById(idInvalido)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.atualizarVeiculo(idInvalido, "XYZ9876", "moto", "modelo", "preto");
        });

        String expectedMessage = "Veículo não encontrado";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(veiculoRepository, times(0)).save(any(Veiculo.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Não deve salvar o veículo com cor inválida")
    void naoDeveSalvarVeiculoComCorInvalida() {
        String corInvalida = "";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.cadastrarVeiculo("XYZ9876", LocalDateTime.now(), "moto", "modelo", corInvalida);
        });

        String expectedMessage = "Cor não pode ser vazia";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(veiculoRepository, times(0)).save(any(Veiculo.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Não deve atualizar o veículo com dados inválidos")
    void naoDeveAtualizarVeiculoComDadosInvalidos() {
        Veiculo veiculoExistente = new Veiculo();
        veiculoExistente.setId(1L);
        veiculoExistente.setPlaca("ABC1234");
        veiculoExistente.setTipoVeiculo("carro");
        veiculoExistente.setModelo("Fusca");
        veiculoExistente.setCor("azul");

        String novaPlaca = "";
        String novoModelo = "Fusca 2.0";
        String novaCor = "verde";
        String novoTipoVeiculo = "carro";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.atualizarVeiculo(1L, novaPlaca, novoTipoVeiculo, novoModelo, novaCor);
        });

        String expectedMessage = "Placa não pode ser vazia";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(veiculoRepository, times(0)).save(any(Veiculo.class));
    }



}
