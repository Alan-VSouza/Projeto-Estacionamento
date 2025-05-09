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
import java.util.List;
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

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        veiculo = createVeiculo();
        estacionamento = createEstacionamento();
        registroEntrada = createRegistroEntrada();
    }

    private Veiculo createVeiculo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA_VEICULO);
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");
        return veiculo;
    }

    private Estacionamento createEstacionamento() {
        Estacionamento estacionamento = new Estacionamento();
        estacionamento.setNome("Estacionamento Central");
        estacionamento.setEndereco("Rua X");
        return estacionamento;
    }

    private RegistroEntrada createRegistroEntrada() {
        RegistroEntrada registroEntrada = new RegistroEntrada();
        registroEntrada.setVeiculo(veiculo);
        registroEntrada.setHoraEntrada(LocalDateTime.now().minusHours(2));
        return registroEntrada;
    }

    @Nested
    @DisplayName("Testes de Registro de Entrada")
    class TestesDeRegistroEntrada {

        @Test
        @Tag("UnitTest")
        @DisplayName("Registrar entrada com sucesso deve salvar registro no repositório")
        void registrarEntrada_comSucesso() {
            UUID estacionamentoId = UUID.randomUUID();

            when(estacionamentoRepository.findById(estacionamentoId))
                    .thenReturn(Optional.of(estacionamento));

            when(registroEntradaRepository.save(any(RegistroEntrada.class)))
                    .thenReturn(new RegistroEntrada(veiculo));

            RegistroEntrada resultado = estacionamentoService.registrarEntrada(veiculo, estacionamentoId);

            assertNotNull(resultado);
            assertEquals(veiculo, resultado.getVeiculo());
            verify(registroEntradaRepository, times(1)).save(any(RegistroEntrada.class));
        }
    }

    @Nested
    @DisplayName("Testes de Cancelamento de Entrada")
    class TestesDeCancelamentoEntrada {

        @Test
        @Tag("UnitTest")
        @DisplayName("Cancelar entrada com sucesso deve deletar registro e retornar true")
        void cancelarEntrada_comSucesso() {
            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                    .thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.of(registroEntrada));

            boolean sucesso = estacionamentoService.cancelarEntrada(PLACA_VEICULO);

            assertTrue(sucesso);
            verify(registroEntradaRepository, times(1)).delete(registroEntrada);
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Deve lançar IllegalArgumentException quando o veículo não estiver registrado ao cancelar entrada")
        void cancelarEntrada_veiculoNaoRegistrado() {
            when(veiculoService.buscarPorPlaca(PLACA_VEICULO)).thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    estacionamentoService.cancelarEntrada(PLACA_VEICULO)
            );

            assertEquals("Veículo não registrado no estacionamento", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Registro de Saída")
    class TestesDeRegistroSaida {

        @Test
        @Tag("UnitTest")
        @DisplayName("Registrar saída com sucesso: gera pagamento e remove entrada")
        void registrarSaida_comSucesso_salvaPagamentoERemoveEntrada() {
            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                    .thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.of(registroEntrada));

            doNothing().when(pagamentoService).salvarPagamento(any(Pagamento.class));

            boolean resultado = estacionamentoService.registrarSaida(PLACA_VEICULO);

            assertTrue(resultado);
            verify(registroEntradaRepository, times(1)).delete(registroEntrada);
            verify(pagamentoService).salvarPagamento(argThat(p ->
                    PLACA_VEICULO.equals(p.getPlaca()) &&
                            p.getHoraEntrada().equals(registroEntrada.getHoraEntrada()) &&
                            p.getHoraSaida() != null
            ));
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Registrar saída retorna false quando não houver registro de entrada")
        void registrarSaida_retornaFalse_quandoSemRegistroEntrada() {
            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                    .thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.empty());

            boolean resultado = estacionamentoService.registrarSaida(PLACA_VEICULO);

            assertFalse(resultado);
            verify(registroEntradaRepository, never()).delete(any());
            verify(pagamentoService, never()).salvarPagamento(any());
        }
    }

    @Nested
    @DisplayName("Testes de Cadastro de Veículo")
    class TestesDeCadastroVeiculo {

        @Test
        @Tag("UnitTest")
        @DisplayName("Deve retornar veículo cadastrado quando ele já existir no sistema")
        void obterOuCadastrarVeiculo_deveRetornarVeiculoCadastrado() {
            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                    .thenReturn(Optional.of(veiculo));

            Veiculo resultado = estacionamentoService.obterOuCadastrarVeiculo(veiculo);

            assertEquals(veiculo, resultado);
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Deve cadastrar e retornar um novo veículo quando ele não existir no sistema")
        void obterOuCadastrarVeiculo_deveCadastrarNovoVeiculo() {
            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                    .thenReturn(Optional.empty());

            Veiculo novoVeiculo = new Veiculo();
            novoVeiculo.setPlaca(PLACA_VEICULO);
            when(veiculoService.cadastrarVeiculo(anyString(), any(), anyString(), anyString(), anyString()))
                    .thenReturn(novoVeiculo);

            Veiculo resultado = estacionamentoService.obterOuCadastrarVeiculo(veiculo);

            assertNotNull(resultado);
            assertEquals(PLACA_VEICULO, resultado.getPlaca());
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Entrada")
    class TestesDeBuscaEntrada {

        @Test
        @Tag("UnitTest")
        @DisplayName("Buscar entrada retorna null quando não houver entrada registrada")
        void buscarEntrada_retornaNull_quandoSemRegistroEntrada() {
            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                    .thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.empty());

            RegistroEntrada resultado = estacionamentoService.buscarEntrada(PLACA_VEICULO);

            assertNull(resultado);
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Estacionamento Atual")
    class TestesDeBuscaEstacionamentoAtual {

        @Test
        @Tag("UnitTest")
        @DisplayName("Buscar estacionamento atual retorna o estacionamento corretamente")
        void buscarEstacionamentoAtual_comSucesso() {
            when(estacionamentoRepository.findAll())
                    .thenReturn(List.of(estacionamento));

            Estacionamento resultado = estacionamentoService.buscarEstacionamentoAtual();

            assertNotNull(resultado);
            assertEquals(estacionamento.getNome(), resultado.getNome());
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Estacionamento")
    class TestesDeBuscaEstacionamento {

        @Test
        @Tag("UnitTest")
        @DisplayName("Buscar estacionamento com sucesso")
        void buscarEstacionamento_comSucesso() {
            UUID estacionamentoId = UUID.randomUUID();
            when(estacionamentoRepository.findById(estacionamentoId))
                    .thenReturn(Optional.of(estacionamento));

            Estacionamento resultado = estacionamentoService.buscarEstacionamento(estacionamentoId);

            assertNotNull(resultado);
            assertEquals(estacionamento.getNome(), resultado.getNome());
            assertEquals(estacionamento.getEndereco(), resultado.getEndereco());
        }

        @Test
        @Tag("UnitTest")
        @DisplayName("Deve lançar IllegalArgumentException quando estacionamento não encontrado")
        void buscarEstacionamento_naoEncontrado() {
            UUID estacionamentoId = UUID.randomUUID();

            when(estacionamentoRepository.findById(estacionamentoId))
                    .thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    estacionamentoService.buscarEstacionamento(estacionamentoId)
            );
            assertEquals("Estacionamento não encontrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Criação de Estacionamento")
    class TestesDeCriacaoEstacionamento {

        @Test
        @Tag("UnitTest")
        @DisplayName("Criar estacionamento com sucesso")
        void criarEstacionamento_comSucesso() {
            Estacionamento estacionamento = new Estacionamento();
            estacionamento.setNome("Estacionamento Central");
            estacionamento.setEndereco("Rua X");

            when(estacionamentoRepository.save(any(Estacionamento.class)))
                    .thenReturn(estacionamento);

            Estacionamento resultado = estacionamentoService.criarEstacionamento(estacionamento);

            assertNotNull(resultado);
            assertEquals(estacionamento.getNome(), resultado.getNome());
            assertEquals(estacionamento.getEndereco(), resultado.getEndereco());
            verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
        }
    }

    @Nested
    @DisplayName("Testes de Obtenção de Entradas")
    class TestesDeGetAllEntradas {

        @Test
        @Tag("UnitTest")
        @DisplayName("Deve retornar uma lista de entradas quando houver registros")
        void getAllEntradas_comRegistros() {
            RegistroEntrada registro1 = new RegistroEntrada();
            registro1.setVeiculo(veiculo);
            registro1.setHoraEntrada(LocalDateTime.now().minusHours(2));

            RegistroEntrada registro2 = new RegistroEntrada();
            registro2.setVeiculo(veiculo);
            registro2.setHoraEntrada(LocalDateTime.now().minusHours(1));

            when(registroEntradaRepository.findAll())
                    .thenReturn(List.of(registro1, registro2));

            List<RegistroEntrada> entradas = estacionamentoService.getAllEntradas();

            assertNotNull(entradas);
            assertEquals(2, entradas.size());
            assertEquals(registro1, entradas.get(0));
            assertEquals(registro2, entradas.get(1));
        }

    }

}
