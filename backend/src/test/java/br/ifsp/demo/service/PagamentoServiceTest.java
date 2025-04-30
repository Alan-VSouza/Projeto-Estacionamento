package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.PagamentoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Nested;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private VeiculoService veiculoService;

    @InjectMocks
    private PagamentoService service;


    private Veiculo veiculo;

    @BeforeEach
    void setUp () {
        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("BQF-2994");
        veiculo.setTipoVeiculo("carro");
        veiculo.setHoraEntrada(LocalDateTime.now().minusHours(2));
        veiculo.setModelo("Escort");
        veiculo.setCor("prata");
    }


    @Nested
    @DisplayName("TDD Tests")
    class TddTests {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve salvar o pagamento")
        void deveSalvarPagamento() {

            Pagamento pagamento = new Pagamento(veiculo);

            service.salvarPagamento(pagamento);

            verify(pagamentoRepository, times(1)).save(any(Pagamento.class));

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve deletar o pagamento")
        void deveDeletarPagamento() {

            Pagamento pagamento = new Pagamento(veiculo);

            when(pagamentoRepository.findById(pagamento.getUuid()))
                    .thenReturn(Optional.of(pagamento));


            service.deletarPagamento(pagamento);

            verify(pagamentoRepository, times(1)).delete(pagamento);

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve atualizar o pagamento")
        void deveAtualizarPagamento() {

            Pagamento pagamento = new Pagamento();
            pagamento.setUuid(UUID.randomUUID());
            pagamento.setVeiculo(veiculo);
            pagamento.setHoraEntrada(veiculo.getHoraEntrada());
            pagamento.setHoraSaida(LocalDateTime.now());
            pagamento.setValor(43);

            when(pagamentoRepository.findById(pagamento.getUuid())).thenReturn(Optional.of(pagamento));

            LocalDateTime novaEntrada = LocalDateTime.now().minusHours(3);
            LocalDateTime novaSaida = LocalDateTime.now().minusHours(1);
            double novoValor = 44;

            Pagamento pagamentoAtualizado = service.atualizarPagamento(pagamento.getUuid(), novaEntrada, novaSaida, veiculo, novoValor);

            assertThat(pagamentoAtualizado.getHoraEntrada()).isEqualTo(novaEntrada);
            assertThat(pagamentoAtualizado.getHoraSaida()).isEqualTo(novaSaida);
            assertThat(pagamentoAtualizado.getVeiculo()).isEqualTo(veiculo);
            assertThat(pagamentoAtualizado.getValor()).isEqualTo(44);

            verify(pagamentoRepository, times(1)).save(any(Pagamento.class));

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve encontrar o pagamento pelo UUID")
        void deveEncontrarPagamentoPeloUuid() {
            UUID uuid = UUID.randomUUID();
            Pagamento pagamento = new Pagamento();
            pagamento.setUuid(uuid);

            when(pagamentoRepository.findById(uuid)).thenReturn(Optional.of(pagamento));

            Pagamento result = service.buscarPorId(uuid);

            assertThat(result).isNotNull();
            assertThat(result.getUuid()).isEqualTo(uuid);
            verify(pagamentoRepository, times(1)).findById(uuid);
        }
    }

    @Nested
    @DisplayName("Testando mensagens de erro")
    class TestandoMensagensDeErro {

        @ParameterizedTest
        @CsvSource(
                value = {
                        "null, 2025-04-30T17:00:00, 20.0, Hora de entrada nao pode ser nula",
                        "2025-04-30T15:30:00, null, 20.0, Hora de saida nao pode ser nula",
                        "2025-04-30T15:30:00, 2025-04-30T17:00:00, -10.0, Valor nao pode ser menor que zero"
                },
                nullValues = "null"
        )
        void mensagemDeErroAoSalvarPagamento(String horaEntrada, String horaSaida, Double valor, String mensagem) {
            LocalDateTime entrada = horaEntrada == null ? null : LocalDateTime.parse(horaEntrada);
            LocalDateTime saida = horaSaida == null ? null : LocalDateTime.parse(horaSaida);

            Pagamento pagamento = new Pagamento();
            pagamento.setUuid(UUID.randomUUID());
            pagamento.setVeiculo(veiculo);
            pagamento.setHoraEntrada(entrada);
            pagamento.setHoraSaida(saida);
            pagamento.setValor(valor);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.salvarPagamento(pagamento));
            assertEquals(mensagem, exception.getMessage());
        }

    }


}