package br.ifsp.demo.service;

import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.VeiculoRepository;
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
    private final VeiculoRepository veiculoRepository;
    private static final int NUMERO_VAGAS = 200;

    @Autowired
    public RelatorioService(PagamentoRepository pagamentoRepository, VeiculoRepository veiculoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public RelatorioDTO gerarRelatorioDesempenho(LocalDate dataReferencia) {
        LocalDateTime inicioDoDia = dataReferencia.atStartOfDay();
        LocalDateTime fimDoDia = dataReferencia.atTime(LocalTime.MAX);

        List<Pagamento> pagamentosDoDia = getPagamentosDoDia(inicioDoDia, fimDoDia);

        int quantidade = pagamentosDoDia.size();
        double tempoTotalMinutos = calcularTempoTotal(pagamentosDoDia);
        double tempoMedioHoras = calcularTempoMedioHoras(quantidade, tempoTotalMinutos);
        double receitaTotal = calcularReceitaTotal(pagamentosDoDia);
        double minutosOcupadosTotal = calcularMinutosOcupados(pagamentosDoDia, inicioDoDia, fimDoDia);

        double ocupacaoMedia = calcularOcupacaoMedia(minutosOcupadosTotal);

        return new RelatorioDTO(quantidade, tempoMedioHoras, receitaTotal, ocupacaoMedia);
    }

    public ReciboDTO gerarRecibo(String placa) {
        return pagamentoRepository.findAll().stream()
                .filter(p -> placa.equals(p.getPlaca()))
                .max(Comparator.comparing(Pagamento::getHoraSaida))
                .map(p -> new ReciboDTO(p.getPlaca(), p.getHoraEntrada(), p.getHoraSaida(), p.getValor()))
                .orElse(null);
    }

    public List<HistoricoDTO> gerarHistorico(String placa) {
        return pagamentoRepository.findAll().stream()
                .filter(p -> placa.equals(p.getPlaca()))
                .map(p -> new HistoricoDTO(p.getPlaca(), p.getHoraEntrada(), p.getHoraSaida(), p.getValor()))
                .toList();
    }

    public int vagasDisponiveis() {
        return NUMERO_VAGAS - veiculoRepository.findAll().size();
    }

    public int vagasOcupadas() {
        return veiculoRepository.findAll().size();
    }

    private List<Pagamento> getPagamentosDoDia(LocalDateTime inicioDoDia, LocalDateTime fimDoDia) {
        return pagamentoRepository.findAll().stream()
                .filter(p -> p.getHoraSaida() != null &&
                        !p.getHoraSaida().isBefore(inicioDoDia) &&
                        !p.getHoraSaida().isAfter(fimDoDia))
                .toList();
    }

    private double calcularTempoTotal(List<Pagamento> pagamentosDoDia) {
        return pagamentosDoDia.stream()
                .filter(p -> p.getHoraEntrada() != null && p.getHoraSaida() != null)
                .mapToDouble(p -> Duration.between(p.getHoraEntrada(), p.getHoraSaida()).toMinutes())
                .sum();
    }

    private double calcularTempoMedioHoras(int quantidade, double tempoTotalMinutos) {
        return Math.round((quantidade > 0 ? (tempoTotalMinutos / quantidade) / 60.0 : 0.0) * 100.0) / 100.0;
    }

    private double calcularReceitaTotal(List<Pagamento> pagamentosDoDia) {
        return pagamentosDoDia.stream()
                .mapToDouble(Pagamento::getValor)
                .sum();
    }

    private double calcularMinutosOcupados(List<Pagamento> pagamentosDoDia, LocalDateTime inicioDoDia, LocalDateTime fimDoDia) {
        return pagamentosDoDia.stream()
                .filter(p -> p.getHoraEntrada() != null && p.getHoraSaida() != null)
                .mapToDouble(p -> {
                    LocalDateTime entrada = p.getHoraEntrada().isBefore(inicioDoDia) ? inicioDoDia : p.getHoraEntrada();
                    LocalDateTime saida = p.getHoraSaida().isAfter(fimDoDia) ? fimDoDia : p.getHoraSaida();
                    return Duration.between(entrada, saida).toMinutes();
                })
                .sum();
    }

    private double calcularOcupacaoMedia(double minutosOcupadosTotal) {
        long minutosNoDia = Duration.between(LocalDateTime.now().toLocalDate().atStartOfDay(), LocalDateTime.now()).toMinutes();
        return (double) Math.round(minutosOcupadosTotal / (minutosNoDia * NUMERO_VAGAS) * 100) / 100;
    }
}
