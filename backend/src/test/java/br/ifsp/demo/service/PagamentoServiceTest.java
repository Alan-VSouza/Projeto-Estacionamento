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
        veiculo.setEntrada(LocalDateTime.now().minusHours(2));

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

}