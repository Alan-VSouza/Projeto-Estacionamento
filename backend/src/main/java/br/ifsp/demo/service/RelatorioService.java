package br.ifsp.demo.service;

import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import br.ifsp.demo.repository.VeiculoRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class RelatorioService {

    private final PagamentoRepository pagamentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final RegistroEntradaRepository relatorioRepository;
    private static final int NUMERO_VAGAS = 200;

    @Autowired
    public RelatorioService(PagamentoRepository pagamentoRepository, VeiculoRepository veiculoRepository, RegistroEntradaRepository relatorioRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.veiculoRepository = veiculoRepository;
        this.relatorioRepository = relatorioRepository;
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
        return NUMERO_VAGAS - (int) relatorioRepository.count();
    }

    public int vagasOcupadas() {
        return (int) relatorioRepository.count();
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

    public String gerarRelatorioCSV(LocalDate data) {
        try {
            RelatorioDTO relatorio = gerarRelatorioDesempenho(data);

            StringWriter sw = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT
                    .withHeader("Métrica", "Valor"));

            csvPrinter.printRecord("Data", data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            csvPrinter.printRecord("Receita Total", "R$ " + String.format("%.2f", relatorio.receitaTotal()));
            csvPrinter.printRecord("Quantidade de Veículos", relatorio.quantidade());
            csvPrinter.printRecord("Tempo Médio (horas)", String.format("%.2f", relatorio.tempoMedioHoras()));
            csvPrinter.printRecord("Ocupação Média", String.format("%.2f%%", relatorio.ocupacaoMedia() * 100));

            csvPrinter.flush();
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }
}
