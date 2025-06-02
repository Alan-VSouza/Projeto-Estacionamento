package br.ifsp.demo.service;

import br.ifsp.demo.dto.CriarEstacionamentoDTO;
import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

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
    private final Integer vagaIdValida = 1;

    private final String PLACA = "BQF-1993";
    private final static int CAPACIDADE = 50;

    @BeforeEach
    void setup() {
        veiculo = new Veiculo(PLACA, "Carro", "Escort", "Prata");
        String NOME_ESTACIONAMENTO = "Estacionamento Carros Velozes";
        String ENDERECO_ESTACIONAMENTO = "Rua Muito Longe";
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

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Buscar entrada retorna null quando não houver entrada registrada")
        void buscarEntrada_retornaNull_quandoSemRegistroEntrada() {
            when(veiculoService.buscarPorPlaca(PLACA)).thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.buscarEntrada(PLACA)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertEquals("Não existe nenhuma entrada registrada nesse veículo", exception.getReason());
        }

        @Nested
        @DisplayName("Testes de Busca de Estacionamento Atual")
        class TestesDeBuscaEstacionamentoAtual {

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Buscar estacionamento atual retorna o estacionamento corretamente")
            void buscarEstacionamentoAtual_comSucesso() {
                when(estacionamentoRepository.findAll()).thenReturn(List.of(estacionamento));

                Estacionamento resultado = estacionamentoService.buscarEstacionamentoAtual();

                assertNotNull(resultado);
                assertEquals(estacionamento.getNome(), resultado.getNome());
            }

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Deve lançar IllegalArgumentException quando nenhum estacionamento atual é encontrado")
            void buscarEstacionamentoAtual_nenhumEncontrado_lancaExcecao() {
                when(estacionamentoRepository.findAll()).thenReturn(Collections.emptyList());

                IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                        estacionamentoService.buscarEstacionamentoAtual()
                );

                assertEquals("Nenhum estacionamento encontrado", exception.getMessage());
            }
        }

        @Nested
        @DisplayName("Testes de Busca de Estacionamento")
        class TestesDeBuscaEstacionamento {

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Buscar estacionamento com sucesso")
            void buscarEstacionamento_comSucesso() {
                UUID estacionamentoIdParaTeste = UUID.randomUUID();

                when(estacionamentoRepository.findById(estacionamentoIdParaTeste))
                        .thenReturn(Optional.of(estacionamento));

                Estacionamento resultado = estacionamentoService.buscarEstacionamento(estacionamentoIdParaTeste);

                assertNotNull(resultado);
                assertEquals(estacionamento.getNome(), resultado.getNome());
                assertEquals(estacionamento.getEndereco(), resultado.getEndereco());
                assertEquals(estacionamento.getCapacidade(), resultado.getCapacidade());
            }

            @Test
            @Tag("UnitTest")
            @DisplayName("Deve lançar IllegalArgumentException quando estacionamento não encontrado")
            void buscarEstacionamento_naoEncontrado() {
                UUID idNaoExistente = UUID.randomUUID();

                when(estacionamentoRepository.findById(idNaoExistente))
                        .thenReturn(Optional.empty());

                ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                        estacionamentoService.buscarEstacionamento(idNaoExistente)
                );

                assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
                assertTrue(Objects.requireNonNull(exception.getReason()).contains("Estacionamento não encontrado com o ID: " + idNaoExistente));

            }
        }

        @Nested
        @DisplayName("Testes de Criação de Estacionamento")
        class TestesDeCriacaoEstacionamento {

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Criar estacionamento com sucesso")
            void criarEstacionamento_comSucesso() {
                CriarEstacionamentoDTO dto = new CriarEstacionamentoDTO(
                        "Estacionamento Beira Mar",
                        "Avenida Litoranea, 777",
                        120
                );

                when(estacionamentoRepository.save(any(Estacionamento.class)))
                        .thenAnswer(invocation -> {
                            estacionamento = invocation.getArgument(0);
                            return estacionamento;
                        });

                Estacionamento resultado = estacionamentoService.criarEstacionamento(dto);

                assertNotNull(resultado);
                assertEquals(dto.nome(), resultado.getNome());
                assertEquals(dto.endereco(), resultado.getEndereco());
                assertEquals(dto.capacidade(), resultado.getCapacidade());

                verify(estacionamentoRepository, times(1)).save(any(Estacionamento.class));
            }
        }

        @Nested
        @DisplayName("Testes de Obtenção de Entradas")
        class TestesDeGetAllEntradas {

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Deve retornar uma lista de todas as entradas quando houver registros")
            void getAllEntradas_comRegistros_retornaLista() {

                Veiculo veiculo2 = new Veiculo("XYZ5678", "Moto", "Bis", "Vermelha");

                RegistroEntrada registro1 = new RegistroEntrada(veiculo, 1);
                RegistroEntrada registro2 = new RegistroEntrada(veiculo2, 2);

                List<RegistroEntrada> listaDeEntradasMock = List.of(registro1, registro2);

                when(registroEntradaRepository.findAll())
                        .thenReturn(listaDeEntradasMock);

                List<RegistroEntrada> resultado = estacionamentoService.getAllEntradas();

                assertNotNull(resultado);
                assertEquals(2, resultado.size());
                assertTrue(resultado.contains(registro1));
                assertTrue(resultado.contains(registro2));
                assertEquals(veiculo.getPlaca(), resultado.get(0).getVeiculo().getPlaca());
                assertEquals(veiculo2.getPlaca(), resultado.get(1).getVeiculo().getPlaca());
            }

            @Test
            @Tag("UnitTest")
            @Tag("Functional")
            @DisplayName("Deve retornar uma lista vazia quando não houver registros de entrada")
            void getAllEntradas_semRegistros_retornaListaVazia() {
                when(registroEntradaRepository.findAll()).thenReturn(Collections.emptyList());

                List<RegistroEntrada> resultado = estacionamentoService.getAllEntradas();

                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Structural Tests")
    class StructuralTests {
        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção ao tentar registrar entrada em vaga já ocupada")
        void registrarEntrada_vagaOcupada_lancaExcecao() {
            UUID estacionamentoId = UUID.randomUUID();
            Integer vagaId = 5;

            when(estacionamentoRepository.findById(estacionamentoId)).thenReturn(Optional.of(estacionamento));
            when(registroEntradaRepository.findByVagaId(vagaId)).thenReturn(Optional.of(new RegistroEntrada(veiculo, vagaId)));

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.registrarEntrada(veiculo, estacionamentoId, vagaId)
            );

            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            assertTrue(Objects.requireNonNull(exception.getReason()).contains("Vaga " + vagaId + " já está ocupada"));
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção quando estacionamento estiver lotado")
        void registrarEntrada_estacionamentoLotado_lancaExcecao() {
            UUID estacionamentoId = UUID.randomUUID();

            estacionamento = new Estacionamento("Teste", "Endereco", 2); // capacidade 2
            when(estacionamentoRepository.findById(estacionamentoId)).thenReturn(Optional.of(estacionamento));
            when(registroEntradaRepository.findByVagaId(anyInt())).thenReturn(Optional.empty());
            when(registroEntradaRepository.count()).thenReturn(2L); // capacidade atingida

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.registrarEntrada(veiculo, estacionamentoId, 1)
            );

            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            assertEquals("Estacionamento lotado. Capacidade máxima atingida.", exception.getReason());
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção quando veículo já possui entrada registrada")
        void registrarEntrada_veiculoJaRegistrado_lancaExcecao() {
            UUID estacionamentoId = UUID.randomUUID();
            Integer vagaId = 10;

            RegistroEntrada entradaExistente = new RegistroEntrada(veiculo, vagaId);

            when(estacionamentoRepository.findById(estacionamentoId)).thenReturn(Optional.of(estacionamento));
            when(registroEntradaRepository.findByVagaId(anyInt())).thenReturn(Optional.empty());
            when(registroEntradaRepository.count()).thenReturn(0L);
            when(veiculoService.buscarPorPlaca(veiculo.getPlaca())).thenReturn(Optional.of(veiculo));
            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(entradaExistente));

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.registrarEntrada(veiculo, estacionamentoId, vagaId)
            );

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertTrue(Objects.requireNonNull(exception.getReason()).contains("Veículo já possui uma entrada registrada na vaga " + vagaId));
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção quando todas as vagas estiverem ocupadas")
        void findNextAvailableSpot_todasVagasOcupadas_lancaExcecao() {
            List<Integer> todasVagas = new java.util.ArrayList<>();
            for (int i = 1; i <= 200; i++) {
                todasVagas.add(i);
            }

            when(registroEntradaRepository.findAllOccupiedSpotIds()).thenReturn(todasVagas);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                    estacionamentoService.findNextAvailableSpot()
            );

            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            assertEquals("Todas as vagas estão ocupadas", exception.getReason());
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção ao tentar criar estacionamento com DTO nulo")
        void criarEstacionamento_dtoNulo_lancaExcecao() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    estacionamentoService.criarEstacionamento(null)
            );

            assertEquals("Dados de criação do estacionamento não podem ser nulos", exception.getMessage());
        }

        @Test
        @Tag("Structural")
        @Tag("UnitTest")
        @DisplayName("Deve lançar exceção quando ID do estacionamento for nulo")
        void buscarEstacionamento_idNulo_lancaExcecao() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    estacionamentoService.buscarEstacionamento(null)
            );

            assertEquals("O ID do estacionamento não pode ser nulo.", exception.getMessage());
        }

        @Test
        @DisplayName("Registrar entrada: veículo existente mas sem entrada registrada - deve continuar normalmente")
        void registrarEntrada_veiculoExistenteSemEntradaRegistrada() {
            UUID estacionamentoId = UUID.randomUUID();
            Integer vagaId = 7;

            when(estacionamentoRepository.findById(estacionamentoId)).thenReturn(Optional.of(estacionamento));
            when(registroEntradaRepository.findByVagaId(vagaId)).thenReturn(Optional.empty());
            when(registroEntradaRepository.count()).thenReturn(0L);

            when(veiculoService.buscarPorPlaca(veiculo.getPlaca())).thenReturn(Optional.of(veiculo));
            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

            when(veiculoService.obterOuCadastrarVeiculo(veiculo)).thenReturn(veiculo);
            when(registroEntradaRepository.save(any(RegistroEntrada.class))).thenAnswer(invocation -> invocation.getArgument(0));

            RegistroEntrada resultado = estacionamentoService.registrarEntrada(veiculo, estacionamentoId, vagaId);

            assertNotNull(resultado);
            assertEquals(veiculo.getPlaca(), resultado.getVeiculo().getPlaca());
            assertEquals(vagaId, resultado.getVagaId());
            verify(registroEntradaRepository).save(any(RegistroEntrada.class));
        }
    }

    @Nested
    @DisplayName("Testes estruturais para validações de mensagem de erro")
    class TestesEstruturaisParaValidacaoDeMensagemDeErro {

        @ParameterizedTest
        @Tag("Structural")
        @Tag("UnitTest")
        @CsvSource(
                value = {
                        "NULL_VAL,   EXISTE_ID,            10,         Veiculo não pode ser nulo",
                        "VALIDO,     NULL_VAL,             10,         ID do estacionamento não pode ser nulo",
                        "VALIDO,     EXISTE_ID,            NULL_VAL,   Número da vaga não pode ser nulo",
                        "VALIDO,     EXISTE_ID,            0,          Número da vaga deve ser maior que zero",
                        "VALIDO,     EXISTE_ID,            -1,         Número da vaga deve ser maior que zero"
                },
                nullValues = {"NULL_VAL"}
        )
        @DisplayName("Deve lançar IllegalArgumentException para parâmetros inválidos")
        void registrarEntrada_comParametrosInvalidos_lancaIllegalArgumentException(
                String veiculoStr,
                String idEstacionamentoStr,
                String vagaIdStr,
                String mensagemEsperada
        ) {
            Veiculo veiculoParam = "VALIDO".equals(veiculoStr) ? veiculo : null;

            UUID idEstacionamentoParam = "EXISTE_ID".equals(idEstacionamentoStr) ? UUID.randomUUID() :
                    (idEstacionamentoStr == null ? null : UUID.fromString(idEstacionamentoStr));

            Integer vagaIdParam = "NULL_VAL".equals(vagaIdStr) ? null :
                    (vagaIdStr == null ? null : Integer.parseInt(vagaIdStr));


            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    estacionamentoService.registrarEntrada(veiculoParam, idEstacionamentoParam, vagaIdParam)
            );

            assertEquals(mensagemEsperada, exception.getMessage());

            verify(estacionamentoRepository, never()).findById(any());
            verify(registroEntradaRepository, never()).save(any());
        }

        @ParameterizedTest
        @Tag("Structural")
        @Tag("UnitTest")
        @CsvSource(
                value = {
                        "NULL_VAL,   EXISTE_ID,        Veiculo para registro não pode ser nulo",
                        "VALIDO,     NULL_VAL,         ID do estacionamento não pode ser nulo",
                        },
                nullValues = {"NULL_VAL"}
        )
        @DisplayName("Deve lançar IllegalArgument para parametros inválidos ao registrar estacionamento")
        void deveLancarIllegalArgumentParaParametrosInvalidosAoRegistrarEstacionamento(
                String veiculoStr,
                String idEstacinamentoStr,
                String mensagem
        ) {
            Veiculo veiculoParam = "VALIDO".equals(veiculoStr) ? veiculo : null;

            UUID idEstacionamentoParam = "EXISTE_ID".equals(idEstacinamentoStr) ? UUID.randomUUID() :
                    (idEstacinamentoStr == null ? null : UUID.fromString(idEstacinamentoStr));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                estacionamentoService.registrar(veiculoParam, idEstacionamentoParam)
            );

            assertEquals(mensagem, exception.getMessage());
            verify(estacionamentoRepository, never()).save(any());

        }

        @ParameterizedTest
        @Tag("Structural")
        @Tag("UnitTest")
        @CsvSource(
                value = {
                        "NULL_VAL,        Placa não pode ser nula ou vazia",
                        "''      ,         Placa não pode ser nula ou vazia",
                },
                nullValues = {"NULL_VAL"}
        )
        @DisplayName("Deve lançar IllegalArgument para parametros inválidos ao registrar saida")
        void deveLancarIllegalArgumentParaParametrosInvalidosAoRegistrarSaida(
                String placa,
                String mensagem
        ) {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    estacionamentoService.registrarSaida(placa));

            assertEquals(mensagem, exception.getMessage());
            verify(estacionamentoRepository, never()).save(any());
            verify(estacionamentoRepository, never()).delete(any());
        }
    }





}