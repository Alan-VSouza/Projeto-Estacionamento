package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PagamentoService {

    public final PagamentoRepository pagamentoRepository;

    @Autowired

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public void salvarPagamento(Pagamento pagamento) {
        if(pagamento == null)
            throw new IllegalArgumentException("Pagamento nao pode ser nulo");

        if(pagamento.getVeiculo() == null)
            throw new IllegalArgumentException("Veiculo nao pode ser nulo");

        if(pagamento.getHoraEntrada() == null)
            throw new IllegalArgumentException("Hora de entrada nao pode ser nulo");

        if(pagamento.getHoraSaida() == null)
            throw new IllegalArgumentException("Hora de saida nao pode ser nulo");

        pagamentoRepository.save(pagamento);
    }

    public void deletarPagamento(Pagamento pagamento) {

        if(pagamento == null)
            throw new IllegalArgumentException("Pagamento nao pode ser nulo");

        if(pagamentoRepository.findById(pagamento.getUuid()).isEmpty())
            throw new IllegalArgumentException("Pagamento nao encontrado");

        pagamentoRepository.delete(pagamento);

    }

    public void atualizarPagamento(Pagamento pagamento) {
        pagamentoRepository.delete(pagamento);
        pagamentoRepository.save(pagamento);
    }

    public Pagamento buscarPorId(UUID uuid) {
        return pagamentoRepository.findById(uuid).orElse(null);
    }



}
