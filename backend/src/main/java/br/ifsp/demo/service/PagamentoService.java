package br.ifsp.demo.service;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.PagamentoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;


    public void salvarPagamento(Pagamento pagamento) {
        if (pagamento == null) {
            throw new IllegalArgumentException("O objeto de pagamento não pode ser nulo.");
        }
        pagamentoRepository.save(pagamento);
    }

    public void deletarPagamento(UUID uuid) {
        if(uuid == null)
            throw new IllegalArgumentException("UUID não pode ser nulo");

        if (!pagamentoRepository.existsById(uuid)) {
            throw new IllegalArgumentException("Pagamento com UUID " + uuid + " não encontrado.");
        }
        pagamentoRepository.deleteById(uuid);
    }

    public Pagamento buscarPorId(UUID uuid) {
        if (uuid == null)
            throw new IllegalArgumentException("UUID não pode ser nulo");

        return pagamentoRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento com UUID " + uuid + " não encontrado."));
    }

    public List<Pagamento> buscarPorData(LocalDate data) {
        if(data ==  null)
            throw new IllegalArgumentException("Data não pode ser nula");

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);
        return pagamentoRepository.findByHoraSaidaBetween(inicio, fim);
    }

    public double calcularTotalArrecadadoPorData(LocalDate data) {
        if(data == null)
            throw new IllegalArgumentException("Data não pode ser nula");

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(23, 59, 59);
        Double total = pagamentoRepository.somarPagamentosPorData(inicio, fim);
        return total != null ? total : 0.0;
    }
}