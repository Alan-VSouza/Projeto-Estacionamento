package br.ifsp.demo.service;

import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
public class RelatorioService {

    private final PagamentoRepository pagamentoRepository;

    @Autowired
    public RelatorioService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public RelatorioDTO gerarRelatorioDesempenho(LocalDate dataReferencia) {
        LocalDateTime inicioDoDia = dataReferencia.atStartOfDay();
        LocalDateTime fimDoDia = dataReferencia.atTime(LocalTime.MAX);

        List<Pagamento> pagamentosDoDia = pagamentoRepository.findAll().stream()
                .filter(p -> p.getHoraSaida() != null &&
                        !p.getHoraSaida().isBefore(inicioDoDia) &&
                        !p.getHoraSaida().isAfter(fimDoDia))
                .toList();

        int quantidade = pagamentosDoDia.size();

        double tempoTotalMinutos = pagamentosDoDia.stream()
                .filter(p -> p.getHoraEntrada() != null && p.getHoraSaida() != null)
                .mapToDouble(p -> Duration.between(p.getHoraEntrada(), p.getHoraSaida()).toMinutes())
                .sum();

        double tempoMedioHoras = quantidade > 0 ? (tempoTotalMinutos / quantidade) / 60.0 : 0.0;

        double receitaTotal = pagamentosDoDia.stream()
                .mapToDouble(Pagamento::getValor)
                .sum();

        double ocupacaoMedia = 0.0;

        return new RelatorioDTO(
                quantidade,
                tempoMedioHoras,
                receitaTotal,
                ocupacaoMedia
        );
    }

    public ReciboDTO gerarRecibo(String placa) {
        return pagamentoRepository.findAll().stream()
                .filter(p -> placa.equals(p.getPlaca()))
                .max(Comparator.comparing(Pagamento::getHoraSaida))
                .map(p -> new ReciboDTO(p.getPlaca(), p.getHoraEntrada(), p.getHoraSaida(), p.getValor()))
                .orElse(null);
    }

    public List<HistoricoDTO> gerarHistorico(String placa) {
        return null;
    }
}