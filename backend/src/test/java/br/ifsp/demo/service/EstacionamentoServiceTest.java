package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.apache.coyote.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstacionamentoServiceTest {

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private VeiculoService veiculoService;

    @Mock
    private CalculadoraDeTarifa calculadoraDeTarifa;

    @InjectMocks
    private EstacionamentoService estacionamentoService;

    private Veiculo veiculo;
    private Estacionamento estacionamento;
    private RegistroEntrada registroEntrada;
    private Integer vagaIdValida = 1;

    private final String PLACA = "BQF-1993";
    private final String NOME_ESTACIONAMENTO = "Estacionamento Carros Velozes";
    private final String ENDERECO_ESTACIONAMENTO = "Rua Muito Longe";
    private final static int CAPACIDADE = 50;

    @BeforeEach
    void setup() {
        veiculo = new Veiculo(PLACA, "Carro", "Escort", "Prata");
        estacionamento = new Estacionamento(NOME_ESTACIONAMENTO, ENDERECO_ESTACIONAMENTO, CAPACIDADE);
        registroEntrada = new RegistroEntrada(veiculo, vagaIdValida);
    }

    @Nested
    @DisplayName("Testes de Registro de Entrada")
    class TestesDeRegistroEntrada {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Registrar entrada com sucesso deve salvar registro no repositório")
        void registrarEntrada_comSucesso() {
            UUID estacionamentoId = UUID.randomUUID();

            when(estacionamentoRepository.findById(estacionamentoId))
                    .thenReturn(Optional.of(estacionamento));

            when(veiculoService.buscarPorPlaca(PLACA)).thenReturn(Optional.empty());
            when(veiculoService.obterOuCadastrarVeiculo(veiculo)).thenReturn(veiculo);
            when(registroEntradaRepository.count()).thenReturn(0L);
            when(registroEntradaRepository.findAllOccupiedSpotIds()).thenReturn(Collections.emptyList());

            when(registroEntradaRepository.save(any(RegistroEntrada.class))).thenAnswer(invocation -> invocation.getArgument(0));

            RegistroEntrada resultado = estacionamentoService.registrar(veiculo, estacionamentoId);

            assertNotNull(resultado);
            assertEquals(veiculo, resultado.getVeiculo());
            assertEquals(vagaIdValida, resultado.getVagaId());
            verify(estacionamentoRepository).findById(estacionamentoId);
            verify(veiculoService).obterOuCadastrarVeiculo(veiculo);
            verify(registroEntradaRepository).count();
            verify(registroEntradaRepository, times(1)).save(any(RegistroEntrada.class));
        }
    }

    @Nested
    @DisplayName("Testes de Cancelamento de Entrada")
    class TestesDeCancelamentoEntrada {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Cancelar entrada com sucesso deve deletar registro e retornar true")
        void cancelarEntrada_comSucesso() {
            when(veiculoService.buscarPorPlaca(PLACA))
                    .thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.of(registroEntrada));

            boolean sucesso = estacionamentoService.cancelarEntrada(PLACA);

            assertTrue(sucesso);
            verify(registroEntradaRepository, times(1)).delete(registroEntrada);
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve lançar IllegalArgumentException quando o veículo não estiver registrado ao cancelar entrada")
        void cancelarEntrada_veiculoNaoRegistrado() {
            when(veiculoService.buscarPorPlaca(PLACA)).thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.cancelarEntrada(PLACA)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Veículo não possui entrada registrada para cancelar", exception.getReason());
            verify(registroEntradaRepository, never()).delete(any(RegistroEntrada.class));
        }
    }

    @Nested
    @DisplayName("Testes de Registro de Saída")
    class TestesDeRegistroSaida {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Registrar saída com sucesso: gera pagamento e remove entrada")
        void registrarSaida_comSucesso_salvaPagamentoERemoveEntrada() {

            LocalDateTime horaEntrada = registroEntrada.getHoraEntrada();
            double valorCalculadoEsperado = 20.0;

            when(calculadoraDeTarifa.calcularValor(eq(horaEntrada), any(LocalDateTime.class))).thenReturn(valorCalculadoEsperado);
            when(estacionamentoRepository.findAll()).thenReturn(List.of(estacionamento));

            when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(veiculoService.buscarPorPlaca(PLACA)).thenReturn(Optional.of(veiculo));
            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));

            Pagamento pagamentoResultado = estacionamentoService.registrarSaida(PLACA);

            assertNotNull(pagamentoResultado);
            assertEquals(PLACA, pagamentoResultado.getPlaca());
            assertEquals(horaEntrada, pagamentoResultado.getHoraEntrada());
            assertNotNull(pagamentoResultado.getHoraSaida());
            assertEquals(valorCalculadoEsperado, pagamentoResultado.getValor());

            verify(registroEntradaRepository, times(1)).delete(registroEntrada);
            verify(pagamentoRepository, times(1)).save(any(Pagamento.class));

        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve lançar ResponseStatusException NOT_FOUND quando não houver registro de entrada ao registrar saída")
        void registrarSaida_lancaExcecao_quandoSemRegistroEntrada() {
            when(estacionamentoRepository.findAll()).thenReturn(List.of(estacionamento));

            when(veiculoService.buscarPorPlaca(PLACA)).thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.registrarSaida(PLACA)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Nenhum registro de entrada ativo para esse veículo", exception.getReason());

            verify(registroEntradaRepository, never()).delete(any());
            verify(pagamentoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Entrada")
    class TestesDeBuscaEntrada {

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar RegistroEntrada quando encontrado")
        void buscarEntrada_encontrado_retornaRegistro() {
            when(veiculoService.buscarPorPlaca(PLACA))
                    .thenReturn(Optional.of(veiculo));
            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.of(registroEntrada));

            RegistroEntrada resultado = estacionamentoService.buscarEntrada(PLACA);

            assertNotNull(resultado);
            assertEquals(registroEntrada, resultado);
            assertEquals(PLACA, resultado.getVeiculo().getPlaca());
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve lançar ResponseStatusException NOT_FOUND quando registro de entrada não encontrado para veículo existente")
        void buscarEntrada_registroNaoEncontrado_lancaExcecao() {
            when(veiculoService.buscarPorPlaca(PLACA))
                    .thenReturn(Optional.of(veiculo));
            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.buscarEntrada(PLACA)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Não existe nenhuma entrada registrada nesse veículo", exception.getReason());
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve lançar ResponseStatusException NOT_FOUND quando veículo não encontrado")
        void buscarEntrada_veiculoNaoEncontrado_lancaExcecao() {
            when(veiculoService.buscarPorPlaca(PLACA))
                    .thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.buscarEntrada(PLACA)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Esse veículo não está no estacionamento", exception.getReason());
            verify(registroEntradaRepository, never()).findByVeiculo(any(Veiculo.class));
        }
    }
//
//    @Nested
//    @DisplayName("Testes de Busca de Entrada")
//    class TestesDeBuscaEntrada {
//
//        @Test
//        @Tag("UnitTest")
//        @Tag("Functional")
//        @DisplayName("Buscar entrada retorna null quando não houver entrada registrada")
//        void buscarEntrada_retornaNull_quandoSemRegistroEntrada() {
//            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
//                    .thenReturn(Optional.of(veiculo));
//
//            when(registroEntradaRepository.findByVeiculo(veiculo))
//                    .thenReturn(Optional.empty());
//
//            RegistroEntrada resultado = estacionamentoService.buscarEntrada(PLACA_VEICULO);
//
//            assertNull(resultado);
//        }
//    }
//
//    @Nested
//    @DisplayName("Testes de Busca de Estacionamento Atual")
//    class TestesDeBuscaEstacionamentoAtual {
//
//        @Test
//        @Tag("UnitTest")
//        @Tag("Functional")
//        @DisplayName("Buscar estacionamento atual retorna o estacionamento corretamente")
//        void buscarEstacionamentoAtual_comSucesso() {
//            when(estacionamentoRepository.findAll())
//                    .thenReturn(List.of(estacionamento));
//
//            Estacionamento resultado = estacionamentoService.buscarEstacionamentoAtual();
//
//            assertNotNull(resultado);
//            assertEquals(estacionamento.getNome(), resultado.getNome());
//        }
//    }
//
//    @Nested
//    @DisplayName("Testes de Busca de Estacionamento")
//    class TestesDeBuscaEstacionamento {
//
//        @Test
//        @Tag("UnitTest")
//        @Tag("Functional")
//        @DisplayName("Buscar estacionamento com sucesso")
//        void buscarEstacionamento_comSucesso() {
//            UUID estacionamentoId = UUID.randomUUID();
//            when(estacionamentoRepository.findById(estacionamentoId))
//                    .thenReturn(Optional.of(estacionamento));
//
//            Estacionamento resultado = estacionamentoService.buscarEstacionamento(estacionamentoId);
//
//            assertNotNull(resultado);
//            assertEquals(estacionamento.getNome(), resultado.getNome());
//            assertEquals(estacionamento.getEndereco(), resultado.getEndereco());
//        }
//
//        @Test
//        @Tag("UnitTest")
//        @DisplayName("Deve lançar IllegalArgumentException quando estacionamento não encontrado")
//        void buscarEstacionamento_naoEncontrado() {
//            UUID estacionamentoId = UUID.randomUUID();
//
//            when(estacionamentoRepository.findById(estacionamentoId))
//                    .thenReturn(Optional.empty());
//
//            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//                    estacionamentoService.buscarEstacionamento(estacionamentoId)
//            );
//            assertEquals("Estacionamento não encontrado", exception.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("Testes de Criação de Estacionamento")
//    class TestesDeCriacaoEstacionamento {
//
//        @Test
//        @Tag("UnitTest")
//        @Tag("Functional")
//        @DisplayName("Criar estacionamento com sucesso")
//        void criarEstacionamento_comSucesso() {
//            Estacionamento estacionamento = new Estacionamento();
//            estacionamento.setNome("Estacionamento Central");
//            estacionamento.setEndereco("Rua X");
//
//            when(estacionamentoRepository.save(any(Estacionamento.class)))
//                    .thenReturn(estacionamento);
//
//            Estacionamento resultado = estacionamentoService.criarEstacionamento(estacionamento);
//
//            assertNotNull(resultado);
//            assertEquals(estacionamento.getNome(), resultado.getNome());
//            assertEquals(estacionamento.getEndereco(), resultado.getEndereco());
//            verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("Testes de Obtenção de Entradas")
//    class TestesDeGetAllEntradas {
//
//        @Test
//        @Tag("UnitTest")
//        @Tag("Functional")
//        @DisplayName("Deve retornar uma lista de entradas quando houver registros")
//        void getAllEntradas_comRegistros() {
//            RegistroEntrada registro1 = new RegistroEntrada();
//            registro1.setVeiculo(veiculo);
//            registro1.setHoraEntrada(LocalDateTime.now().minusHours(2));
//
//            RegistroEntrada registro2 = new RegistroEntrada();
//            registro2.setVeiculo(veiculo);
//            registro2.setHoraEntrada(LocalDateTime.now().minusHours(1));
//
//            when(registroEntradaRepository.findAll())
//                    .thenReturn(List.of(registro1, registro2));
//
//            List<RegistroEntrada> entradas = estacionamentoService.getAllEntradas();
//
//            assertNotNull(entradas);
//            assertEquals(2, entradas.size());
//            assertEquals(registro1, entradas.get(0));
//            assertEquals(registro2, entradas.get(1));
//        }
//    }
}