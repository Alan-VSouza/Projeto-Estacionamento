package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    public final PagamentoRepository pagamentoRepository;

    @Autowired

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public void salvarPagamento(Veiculo veiculo) {
        Pagamento pagamento = new Pagamento(veiculo);
        pagamentoRepository.save(pagamento);
    }
}
