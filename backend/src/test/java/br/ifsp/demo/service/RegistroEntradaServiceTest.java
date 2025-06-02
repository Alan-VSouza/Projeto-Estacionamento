package br.ifsp.demo.service;

import br.ifsp.demo.components.LogSistema;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistroEntradaServiceTest {

    private static final String PLACA_VEICULO = "ABC1234";
    private static final String MENSAGEM_VEICULO_JA_REGISTRADO = "Veículo já registrado no estacionamento";
    private static final String MOTIVO_CANCELAMENTO = "Cancelamento feito por engano";

    @Mock
    private VeiculoService veiculoService;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @Mock
    private LogSistema logSistema;

    @InjectMocks
    private RegistroEntradaService registroEntradaService;

    private Veiculo veiculo;
    private RegistroEntrada registroEntrada;

    @BeforeEach
    void setup() {
        veiculo = new Veiculo(PLACA_VEICULO, "Carro", "Fusca", "Azul");
        registroEntrada = new RegistroEntrada(veiculo);
    }


    @Nested
    @DisplayName("Testes de Registro de Entrada")
    class TestesDeRegistroEntrada {

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Deve registrar entrada com sucesso quando veículo não estiver registrado")
        void registrarEntrada_comSucesso() {
            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

            when(registroEntradaRepository.save(any(RegistroEntrada.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            RegistroEntrada resultado = registroEntradaService.registrarEntrada(veiculo);

            assertNotNull(resultado);
            assertEquals(veiculo, resultado.getVeiculo());
            assertNotNull(resultado.getHoraEntrada());
            assertNotNull(resultado.getVagaId());

            verify(registroEntradaRepository, times(1)).findByVeiculo(veiculo);
            verify(registroEntradaRepository, times(1)).save(any(RegistroEntrada.class));
        }

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @Tag("TDD")
        @DisplayName("Deve lançar IllegalArgumentException quando o veículo já estiver registrado")
        void registrarEntrada_veiculoJaRegistrado() {
            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> registroEntradaService.registrarEntrada(veiculo));

            assertEquals(MENSAGEM_VEICULO_JA_REGISTRADO, ex.getMessage());
            verify(registroEntradaRepository, times(0)).save(any());
        }

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @DisplayName("Deve registrar corretamente o horário de entrada do veículo")
        void registrarEntrada_comHorarioCorreto() {
            when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

            when(registroEntradaRepository.save(any(RegistroEntrada.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            LocalDateTime antesDaChamada = LocalDateTime.now();
            RegistroEntrada resultado = registroEntradaService.registrarEntrada(veiculo);
            LocalDateTime depoisDaChamada = LocalDateTime.now();

            assertNotNull(resultado);
            assertEquals(veiculo, resultado.getVeiculo());
            assertNotNull(resultado.getHoraEntrada());

            assertTrue(resultado.getHoraEntrada().equals(antesDaChamada) || resultado.getHoraEntrada().isAfter(antesDaChamada),
                    "Hora de entrada deveria ser igual ou depois do momento antes da chamada.");
            assertTrue(resultado.getHoraEntrada().equals(depoisDaChamada) || resultado.getHoraEntrada().isBefore(depoisDaChamada),
                    "Hora de entrada deveria ser igual ou antes do momento depois da chamada.");

            verify(registroEntradaRepository, times(1)).findByVeiculo(veiculo);
            verify(registroEntradaRepository, times(1)).save(any(RegistroEntrada.class));
        }
    }

    @Nested
    @DisplayName("Testes de Cancelamento de Entrada")
    class TestesDeCancelamentoEntrada {

        @Test
        @Tag("Functional")
        @Tag("UnitTest")
        @DisplayName("Deve cancelar check-in com sucesso")
        void cancelarCheckIn_comSucesso() {

            when(veiculoService.buscarPorPlaca(PLACA_VEICULO))
                    .thenReturn(Optional.of(veiculo));

            when(registroEntradaRepository.findByVeiculo(veiculo))
                    .thenReturn(Optional.of(registroEntrada));


            boolean sucesso = registroEntradaService.cancelarCheckIn(veiculo, MOTIVO_CANCELAMENTO);

            assertTrue(sucesso);
            verify(registroEntradaRepository, times(1)).delete(registroEntrada);
            verify(logSistema, times(1)).registrarCancelamento(PLACA_VEICULO, MOTIVO_CANCELAMENTO);
        }
    }

    @Nested
    @DisplayName("Testes de Mutante")
    class TestesDeMutante {

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção quando veículo não for encontrado ao cancelar check-in")
        void cancelarCheckIn_quandoVeiculoNaoEncontrado_deveLancarExcecao() {

            String placaVeiculo = "XYZ-1234";
            Veiculo veiculo1 = new Veiculo(placaVeiculo, "carro", "corolla","branco" );

            when(veiculoService.buscarPorPlaca(placaVeiculo)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                registroEntradaService.cancelarCheckIn(veiculo1, MOTIVO_CANCELAMENTO);
            });

            assertThat(exception.getMessage()).isEqualTo("Veículo não encontrado");
        }

        @Test
        @Tag("UnitTest")
        @Tag("Mutation")
        @DisplayName("Deve lançar exceção quando Veículo é encontrado MAS não possui RegistroEntrada, ao cancelar check-in")
        void cancelarCheckIn_quandoVeiculoEncontradoMasRegistroEntradaNao_deveLancarExcecao() {
            String placaVeiculo = "ABC-7890";
            Veiculo veiculoExistenteMock = mock(Veiculo.class);
            String motivoCancelamento = "Teste";

            when(veiculoService.buscarPorPlaca(placaVeiculo)).thenReturn(Optional.of(veiculoExistenteMock));

            when(registroEntradaRepository.findByVeiculo(veiculoExistenteMock))
                    .thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                registroEntradaService.cancelarCheckIn(veiculoExistenteMock, MOTIVO_CANCELAMENTO);
            });

            assertThat(exception.getMessage()).isEqualTo("Veículo não registrado no estacionamento");
        }

    }
}