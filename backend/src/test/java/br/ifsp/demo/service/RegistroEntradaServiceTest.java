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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistroEntradaServiceTest {

    private static final String PLACA_VEICULO = "ABC1234";
    private static final String MENSAGEM_VEICULO_NAO_ENCONTRADO = "Veículo não encontrado";
    private static final String MENSAGEM_VEICULO_JA_REGISTRADO = "Veículo já registrado no estacionamento";

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
        veiculo = criarVeiculo();
        registroEntrada = criarRegistroEntrada(veiculo);
    }

    private Veiculo criarVeiculo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA_VEICULO);
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");
        return veiculo;
    }

    private RegistroEntrada criarRegistroEntrada(Veiculo veiculo) {
        RegistroEntrada registroEntrada = new RegistroEntrada();
        registroEntrada.setVeiculo(veiculo);
        return registroEntrada;
    }

    @Test
    @DisplayName("Deve registrar entrada com sucesso quando veículo não estiver registrado")
    public void registrarEntrada_comSucesso() {
        when(veiculoRepository.findByPlaca(PLACA_VEICULO)).thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());
        when(registroEntradaRepository.save(any())).thenReturn(registroEntrada);

        RegistroEntrada resultado = registroEntradaService.registrarEntrada(PLACA_VEICULO);

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());
        verify(registroEntradaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando veículo não encontrado")
    public void registrarEntrada_veiculoNaoEncontrado() {
        when(veiculoRepository.findByPlaca(PLACA_VEICULO)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> registroEntradaService.registrarEntrada(PLACA_VEICULO));
        assertEquals(MENSAGEM_VEICULO_NAO_ENCONTRADO, thrown.getMessage());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando o veículo já estiver registrado")
    public void registrarEntrada_veiculoJaRegistrado() {
        when(veiculoRepository.findByPlaca(PLACA_VEICULO)).thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> registroEntradaService.registrarEntrada(PLACA_VEICULO));

        assertEquals(MENSAGEM_VEICULO_JA_REGISTRADO, ex.getMessage());
        verify(registroEntradaRepository, times(0)).save(any());
    }
}
