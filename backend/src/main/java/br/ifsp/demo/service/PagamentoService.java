package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
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

    public void salvarPagamento(Pagamento pagamento) {
        pagamentoRepository.save(pagamento);
    }

    public void deletarPagamento(Pagamento pagamento) {
        pagamentoRepository.delete(pagamento);
    }

    public void atualizarPagamento(Pagamento pagamento) {
        pagamentoRepository.delete(pagamento);
        pagamentoRepository.save(pagamento);
    }



}
