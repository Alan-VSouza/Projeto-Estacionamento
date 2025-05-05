package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstacionamentoServiceTest {

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @Mock
    private PagamentoService pagamentoService;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private VeiculoService veiculoService;

    @InjectMocks
    private EstacionamentoService estacionamentoService;

    private Estacionamento estacionamento;
    private Veiculo veiculo;
    private RegistroEntrada registroEntrada;

    private static final String PLACA_VEICULO = "ABC1234";
    private static final String PLACA = PLACA_VEICULO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        veiculo = new Veiculo();
        veiculo.setPlaca(PLACA_VEICULO);
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");

        estacionamento = new Estacionamento();
        estacionamento.setNome("Estacionamento Central");
        estacionamento.setEndereco("Rua X");

        registroEntrada = new RegistroEntrada();
        registroEntrada.setVeiculo(veiculo);
        registroEntrada.setHoraEntrada(LocalDateTime.now().minusHours(2));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Registrar entrada com sucesso deve salvar registro no repositório")
    public void registrarEntrada_comSucesso() {
        UUID estacionamentoId = UUID.randomUUID();

        Estacionamento estacionamento = new Estacionamento();
        estacionamento.setId(estacionamentoId);

        when(estacionamentoRepository.findById(estacionamentoId))
                .thenReturn(Optional.of(estacionamento));

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");

        when(registroEntradaRepository.save(any(RegistroEntrada.class)))
                .thenReturn(new RegistroEntrada(veiculo));

        RegistroEntrada resultado = estacionamentoService.registrarEntrada(veiculo, estacionamentoId);

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());
        verify(registroEntradaRepository, times(1)).save(any(RegistroEntrada.class));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Cancelar entrada com sucesso deve deletar registro e retornar true")
    public void cancelarEntrada_comSucesso() {
        String placa = veiculo.getPlaca();

        when(veiculoService.buscarPorPlaca(placa))
                .thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo))
                .thenReturn(Optional.of(registroEntrada));

        boolean sucesso = estacionamentoService.cancelarEntrada(placa);

        assertTrue(sucesso);
        verify(registroEntradaRepository, times(1)).delete(registroEntrada);
    }


    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve lançar IllegalArgumentException quando o veículo não estiver registrado ao cancelar entrada")
    public void cancelarEntrada_veiculoNaoRegistrado() {
        String placa = PLACA_VEICULO;

        when(veiculoService.buscarPorPlaca(placa)).thenReturn(Optional.of(veiculo));

        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

        when(estacionamentoRepository.findById(estacionamento.getId())).thenReturn(Optional.of(estacionamento));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            estacionamentoService.cancelarEntrada(placa);
        });

        assertEquals("Veículo não registrado no estacionamento", exception.getMessage());
    }


    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Registrar saída com sucesso: gera pagamento e remove entrada")
    public void registrarSaida_comSucesso_salvaPagamentoERemoveEntrada() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA_VEICULO);

        when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                .thenReturn(Optional.of(veiculo));

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculo);
        registroEntrada.setHoraEntrada(LocalDateTime.of(2025, 5, 4, 10, 0));
        when(registroEntradaRepository.findByVeiculo(veiculo))
                .thenReturn(Optional.of(registroEntrada));

        doNothing().when(pagamentoService).salvarPagamento(any(Pagamento.class));

        boolean resultado = estacionamentoService.registrarSaida(veiculo.getPlaca());

        assertTrue(resultado);

        verify(registroEntradaRepository, times(1))
                .delete(registroEntrada);

        verify(pagamentoService).salvarPagamento(argThat(p ->
                PLACA_VEICULO.equals(p.getPlaca()) &&
                        p.getHoraEntrada().equals(registroEntrada.getHoraEntrada()) &&
                        p.getHoraSaida() != null
        ));
    }


    @Test
    @Tag("UnitTest")
    @DisplayName("Registrar saída retorna false quando não houver registro de entrada")
    public void registrarSaida_retornaFalse_quandoSemRegistroEntrada() {
        when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                .thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo))
                .thenReturn(Optional.empty());

        boolean resultado = estacionamentoService.registrarSaida(veiculo.getPlaca());

        assertFalse(resultado);
        verify(registroEntradaRepository, never()).delete(any());
        verify(pagamentoService, never()).salvarPagamento(any());
    }


}
