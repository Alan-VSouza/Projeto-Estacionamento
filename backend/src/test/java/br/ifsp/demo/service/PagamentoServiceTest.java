package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.PagamentoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve salvar o pagamento")
    void deveSalvarPagamento() {

        PagamentoRepository pagamentoRepository = mock(PagamentoRepository.class);
        PagamentoService service = new PagamentoService(pagamentoRepository);

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC-1234");
        veiculo.setHoraEntrada(LocalDateTime.now().minusHours(2));

        Pagamento pagamento = new Pagamento(veiculo);

        service.salvarPagamento(pagamento);

        verify(pagamentoRepository, times(1)).save(any(Pagamento.class));

    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve deletar o pagamento")
    void deveDeletarPagamento() {

        PagamentoRepository pagamentoRepository = mock(PagamentoRepository.class);
        PagamentoService service = new PagamentoService(pagamentoRepository);

        Pagamento pagamento = new Pagamento();
        pagamento.setUuid(UUID.randomUUID());

        service.deletarPagamento(pagamento);

        verify(pagamentoRepository, times(1)).delete(pagamento);

    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve atualizar o pagamento")
    void deveAtualizarPagamento() {

        PagamentoRepository pagamentoRepository = mock(PagamentoRepository.class);
        PagamentoService service = new PagamentoService(pagamentoRepository);

        Pagamento pagamento = new Pagamento();
        pagamento.setUuid(UUID.randomUUID());
        pagamento.setHoraSaida(LocalDateTime.now());
        pagamento.setValor(42.0);

        service.atualizarPagamento(pagamento);

        verify(pagamentoRepository, times(1)).save(pagamento);

    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve encontrar o pagamento pelo UUID")
    void deveEncontrarPagamentoPeloUuid() {
        UUID uuid = UUID.randomUUID();
        Pagamento pagamento = new Pagamento();
        pagamento.setUuid(uuid);

        PagamentoRepository pagamentoRepository = mock(PagamentoRepository.class);
        when(pagamentoRepository.findById(uuid)).thenReturn(Optional.of(pagamento));

        PagamentoService service = new PagamentoService(pagamentoRepository);

        Pagamento result = service.buscarPorId(uuid);

        assertThat(result).isNotNull();
        assertThat(result.getUuid()).isEqualTo(uuid);
        verify(pagamentoRepository, times(1)).findById(uuid);
    }


}