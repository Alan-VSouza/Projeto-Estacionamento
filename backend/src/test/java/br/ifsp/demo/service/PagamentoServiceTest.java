package br.ifsp.demo.service;

import br.ifsp.demo.exception.PagamentoNaoEncontradoException;
import br.ifsp.demo.exception.VeiculoNaoEncontradoException;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.components.CalculadoraTempoPermanencia;
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


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    @Mock
    CalculadoraTempoPermanencia calculadoraTempoPermanencia;

    @InjectMocks
    private PagamentoService service;

    private Veiculo veiculo;
    private Pagamento pagamento;

    @BeforeEach
    void setUp () {
        veiculo = new Veiculo("BQF-2994",
                "carro",
                "Escort",
                "prata",
                LocalDateTime.of(2025,4,30,10,0,0));

        pagamento = new Pagamento(veiculo.getPlaca(),
                veiculo.getHoraEntrada(),
                LocalDateTime.now(),
                43);
    }


    @Nested
    @DisplayName("TDD Tests")
    class TddTests {
        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve salvar o pagamento")
        void deveSalvarPagamento() {

            when(veiculoService.buscarPorPlaca(pagamento.getPlaca())).thenReturn(Optional.ofNullable(veiculo));

            service.salvarPagamento(pagamento);

            verify(pagamentoRepository, times(1)).save(any(Pagamento.class));
            verify(veiculoService).deletarVeiculo(veiculo.getId());
            verify(calculadoraTempoPermanencia, times(1)).calcularValorDaPermanencia(anyInt());

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve deletar o pagamento")
        void deveDeletarPagamento() {

            when(pagamentoRepository.findById(pagamento.getUuid()))
                    .thenReturn(Optional.of(pagamento));


            service.deletarPagamento(pagamento);

            verify(pagamentoRepository, times(1)).delete(pagamento);

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve atualizar o pagamento")
        void deveAtualizarPagamento() {

            when(veiculoService.buscarPorPlaca(pagamento.getPlaca())).thenReturn(Optional.ofNullable(veiculo));
            when(pagamentoRepository.findById(pagamento.getUuid())).thenReturn(Optional.of(pagamento));

            LocalDateTime novaEntrada = LocalDateTime.now().minusHours(3);
            LocalDateTime novaSaida = LocalDateTime.now().minusHours(1);
            double novoValor = 44;

            Pagamento pagamentoAtualizado = service.atualizarPagamento(pagamento.getUuid(), novaEntrada, novaSaida, veiculo.getPlaca(), novoValor);

            assertThat(pagamentoAtualizado.getHoraEntrada()).isEqualTo(novaEntrada);
            assertThat(pagamentoAtualizado.getHoraSaida()).isEqualTo(novaSaida);
            assertThat(pagamentoAtualizado.getPlaca()).isEqualTo(veiculo.getPlaca());
            assertThat(pagamentoAtualizado.getValor()).isEqualTo(44);

            verify(pagamentoRepository, times(1)).save(any(Pagamento.class));

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve encontrar o pagamento pelo UUID")
        void deveEncontrarPagamentoPeloUuid() {
            UUID uuid = pagamento.getUuid();

            when(pagamentoRepository.findById(uuid)).thenReturn(Optional.of(pagamento));

            Pagamento result = service.buscarPorId(uuid);

            assertThat(result).isNotNull();
            assertThat(result.getUuid()).isEqualTo(uuid);
            verify(pagamentoRepository, times(1)).findById(uuid);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Testa salvar pagamento calculando o valor de permanencia")
        void testaSalvarPagamentoCalculandoOValorDePermanencia() {
            pagamento.setHoraSaida(LocalDateTime.of(2025,4,30,13,1,0));

            when(veiculoService.buscarPorPlaca(pagamento.getPlaca())).thenReturn(Optional.ofNullable(veiculo));
            when(calculadoraTempoPermanencia.calcularValorDaPermanencia(anyInt())).thenReturn(26.0);

            service.salvarPagamento(pagamento);

            assertEquals(26.0, pagamento.getValor());

            verify(pagamentoRepository, times(1)).save(pagamento);
            verify(veiculoService, times(1)).deletarVeiculo(veiculo.getId());

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve buscar pagamento por data")
        void deveBuscarPagamentoPorData() {
            LocalDate data = LocalDate.of(2025, 5, 2);

            pagamento.setHoraEntrada(data.atTime(9, 0));
            pagamento.setHoraSaida(data.atTime(10, 0));

            when(pagamentoRepository.findByHoraSaidaBetween(
                    data.atStartOfDay(),
                    data.atTime(23, 59, 59 ))
            ).thenReturn(List.of(pagamento));

            List<Pagamento> resultado = service.buscarPorData(data);

            assertThat(resultado).hasSize(1).containsExactly(pagamento);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar o total arrecadado quando existem pagamentos")
        void deveRetornarTotalArrecadadoQuandoExistemPagamentos() {
            LocalDate data = LocalDate.of(2025, 5, 3);
            LocalDateTime inicio = data.atStartOfDay();
            LocalDateTime fim = data.atTime(23, 59, 59);

            when(pagamentoRepository.somarPagamentosPorData(inicio, fim)).thenReturn(100.0);

            double total = service.calcularTotalArrecadadoPorData(data);

            assertEquals(100.0, total);
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("Deve retornar zero quando nao existirem pagamentos")
        void deveRetornarZeroQuandoNaoExistemPagamentos() {
            LocalDate data = LocalDate.of(2025, 5, 3);
            LocalDateTime inicio = data.atStartOfDay();
            LocalDateTime fim = data.atTime(23, 59, 59);

            when(pagamentoRepository.somarPagamentosPorData(inicio, fim)).thenReturn(null);

            double total = service.calcularTotalArrecadadoPorData(data);

            assertEquals(0.0, total);
        }

    }

    @Nested
    @DisplayName("Testando mensagens de erro")
    class TestandoMensagensDeErro {

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Functional")
        @CsvSource(
                value = {
                        "null, 2025-04-30T17:00:00, 20.0, Hora de entrada não pode ser nula",
                        "2025-04-30T15:30:00, null, 20.0, Hora de saída não pode ser nula",
                },
                nullValues = "null"
        )
        void mensagensDeErroAoSalvarPagamento(String horaEntrada, String horaSaida, Double valor, String mensagem) {
            LocalDateTime entrada = horaEntrada == null ? null : LocalDateTime.parse(horaEntrada);
            LocalDateTime saida = horaSaida == null ? null : LocalDateTime.parse(horaSaida);

            pagamento.setHoraEntrada(entrada);
            pagamento.setHoraSaida(saida);
            pagamento.setValor(valor);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.salvarPagamento(pagamento));
            assertEquals(mensagem, exception.getMessage());

            verify(pagamentoRepository, never()).save(any());
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("mensagem de erro quando veiculo e nulo")
        void mensagemDeErroQuandoVeiculoNulo() {

            pagamento.setPlaca(null);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.salvarPagamento(pagamento));
            assertEquals("Placa não pode ser nula ou vazia", exception.getMessage());
        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("mensgaem de erro ao tentar excluir pagamento nulo")
        void mensagemDeErroAoExcluirPagamentoNulo() {

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> service.deletarPagamento(null));
            assertEquals("Pagamento não pode ser nulo", exception.getMessage());

        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Functional")
        @CsvSource(
                value = {
                        "null, 2025-04-30T15:30:00, 2025-04-30T17:00:00, 15.0, Uuid não pode ser nulo",
                        "123e4567-e89b-12d3-a456-426614174000, null, 2025-04-30T17:00:00, 15.0, Entrada não pode ser nulo",
                        "123e4567-e89b-12d3-a456-426614174000, 2025-04-30T17:00:00, null, 10.0, Saída não pode ser nulo",
                },
                nullValues = "null"
        )
        void mensagensDeErroAoAtualizarPagamento(String uuid, String horaEntrada, String horaSaida, double valor, String mensagem) {
            LocalDateTime entrada = horaEntrada == null ? null : LocalDateTime.parse(horaEntrada);
            LocalDateTime saida = horaSaida == null ? null : LocalDateTime.parse(horaSaida);
            UUID uuidPagamento = uuid == null ? null : UUID.fromString(uuid);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.atualizarPagamento(
                    uuidPagamento, entrada, saida, veiculo.getPlaca(), valor
            ));
            assertEquals(mensagem, exception.getMessage());

        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("mensagem de erro ao atualizar pagamento com veiculo nulo")
        void mensagemDeErroAoAtualizarPagamentoNulo() {

            VeiculoNaoEncontradoException exception = assertThrows(VeiculoNaoEncontradoException.class, ()-> service.atualizarPagamento(
                    UUID.randomUUID(), LocalDateTime.now().minusHours(2), LocalDateTime.now(), null, 0
            ));
            assertEquals("Veículo não existe no banco de dados", exception.getMessage());

        }

        @Test
        @Tag("UnitTest")
        @Tag("Functional")
        @DisplayName("mensagem de erro ao atualizar pagamento com UUID inexistente")
        void mensagemDeErroAoAtualizarPagamentoInexistente() {
            UUID uuidInexistente = UUID.randomUUID();

            when(veiculoService.buscarPorPlaca(pagamento.getPlaca())).thenReturn(Optional.ofNullable(veiculo));
            when(pagamentoRepository.findById(uuidInexistente)).thenReturn(Optional.empty());

            PagamentoNaoEncontradoException exception = assertThrows(PagamentoNaoEncontradoException.class, () ->
                    service.atualizarPagamento(
                            uuidInexistente,
                            LocalDateTime.now().minusHours(1),
                            LocalDateTime.now(),
                            veiculo.getPlaca(),
                            10.0
                    )
            );

            assertEquals("Pagamento não encontrado", exception.getMessage());
            verify(pagamentoRepository).findById(uuidInexistente);
        }

        @ParameterizedTest
        @Tag("UnitTest")
        @Tag("Functional")
        @CsvSource(
                value ={
                "null, Uuid não pode ser nulo",
                "123e4567-e89b-12d3-a456-426614174000, Esse pagamento não existe"
            },
                nullValues = "null"
        )
        @DisplayName("mensagem de erro se if for nulo ou inexistente")
        void mensagemDeErroAoBuscarPagamentoPorUuidNulo(String uuid, String mensagem) {

            UUID uuidPagamento = uuid == null ? null : UUID.fromString(uuid);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.buscarPorId(uuidPagamento));
            assertEquals(mensagem, exception.getMessage());

        }
    }
}