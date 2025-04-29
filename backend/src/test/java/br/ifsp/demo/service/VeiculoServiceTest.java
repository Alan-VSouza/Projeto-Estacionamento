package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
