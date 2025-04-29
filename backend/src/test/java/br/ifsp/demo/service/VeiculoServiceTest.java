package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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



}
