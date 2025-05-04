package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstacionamentoServiceTest {

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @Mock
    private VeiculoService veiculoService;


    @InjectMocks
    private EstacionamentoService estacionamentoService;

    private Estacionamento estacionamento;
    private Veiculo veiculo;
    private RegistroEntrada registroEntrada;

    private static final String PLACA_VEICULO = "ABC1234";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        veiculo = new Veiculo();
        veiculo.setPlaca(PLACA_VEICULO);
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");

        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setNome("Estacionamento Central");
        estacionamento.setEndereco("Rua X");

        registroEntrada = new RegistroEntrada();
        registroEntrada.setVeiculo(veiculo);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Registrar entrada com sucesso deve salvar registro no repositório")
    public void registrarEntrada_comSucesso() {
        when(estacionamentoRepository.findById(1L))
                .thenReturn(Optional.of(estacionamento));
        when(registroEntradaRepository.save(any(RegistroEntrada.class)))
                .thenReturn(new RegistroEntrada(veiculo));

        RegistroEntrada resultado = estacionamentoService.registrarEntrada(veiculo);

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());
        verify(registroEntradaRepository, times(1))
                .save(any(RegistroEntrada.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Cancelar entrada com sucesso deve deletar registro e retornar true")
    public void cancelarEntrada_comSucesso() {
        when(veiculoService.buscarPorPlaca(veiculo.getPlaca()))
                .thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo))
                .thenReturn(Optional.of(registroEntrada));

        boolean sucesso = estacionamentoService.cancelarEntrada(veiculo);

        assertTrue(sucesso);
        verify(registroEntradaRepository, times(1))
                .delete(registroEntrada);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve lançar IllegalArgumentException quando o veículo não estiver registrado ao cancelar entrada")
    public void cancelarEntrada_veiculoNaoRegistrado() {
        when(veiculoService.buscarPorPlaca(PLACA_VEICULO)).thenReturn(Optional.of(veiculo));

        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            estacionamentoService.cancelarEntrada(veiculo);
        });

        assertEquals("Veículo não registrado no estacionamento", exception.getMessage());
    }

}
