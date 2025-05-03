package br.ifsp.demo.service;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import br.ifsp.demo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistroEntradaServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @InjectMocks
    private RegistroEntradaService registroEntradaService;

    private Veiculo veiculo;
    private RegistroEntrada registroEntrada;

    @BeforeEach
    void setup() {
        veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");

        registroEntrada = new RegistroEntrada();
        registroEntrada.setVeiculo(veiculo);
    }

    @Test
    @DisplayName("Deve registrar entrada com sucesso quando veículo não estiver registrado")
    public void registrarEntrada_comSucesso() {
        when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());
        when(registroEntradaRepository.save(any())).thenReturn(registroEntrada);

        RegistroEntrada resultado = registroEntradaService.registrarEntrada("ABC1234");

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());
        verify(registroEntradaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado")
    public void registrarEntrada_veiculoNaoEncontrado() {
        when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> registroEntradaService.registrarEntrada("ABC1234"));

        assertEquals("Veículo não encontrado", thrown.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o veículo já estiver registrado")
    public void registrarEntrada_veiculoJaRegistrado() {
        when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> registroEntradaService.registrarEntrada("ABC1234"));

        assertEquals("Veículo já registrado no estacionamento", ex.getMessage());
        verify(registroEntradaRepository, times(0)).save(any());
    }
}