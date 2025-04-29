package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

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
        veiculo.setAno(1995);

        when(veiculoRepository.save(veiculo)).thenReturn(veiculo);

        Veiculo result = veiculoService.cadastrarVeiculo("ABC1234", "carro", "Fusca", "azul", 1995);

        assertEquals(veiculo.getPlaca(), result.getPlaca());
        assertEquals(veiculo.getModelo(), result.getModelo());

        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }
}
