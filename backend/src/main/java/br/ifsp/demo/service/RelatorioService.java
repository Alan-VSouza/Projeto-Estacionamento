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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class RelatorioService {

    private final PagamentoRepository pagamentoRepository;
    private final RegistroEntradaRepository registroEntradaRepository;
    private static final int NUMERO_VAGAS = 200;

    @Autowired
    public RelatorioService(PagamentoRepository pagamentoRepository,
                            RegistroEntradaRepository registroEntradaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.registroEntradaRepository = registroEntradaRepository;
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
                .sorted(Comparator.comparing(Pagamento::getHoraEntrada).reversed())
                .map(p -> new HistoricoDTO(p.getPlaca(), p.getHoraEntrada(), p.getHoraSaida(), p.getValor()))
                .toList();
    }

    public int vagasDisponiveis() {
        long vagasOcupadas = registroEntradaRepository.count();
        return NUMERO_VAGAS - (int) vagasOcupadas;
    }

    public int vagasOcupadas() {
        return (int) registroEntradaRepository.count();
    }

    public String gerarRelatorioCSV(LocalDate data) {
        try {
            RelatorioDTO relatorio = gerarRelatorioDesempenho(data);

            StringWriter sw = new StringWriter();
            CSVFormat format = CSVFormat.DEFAULT
                    .withHeader("Métrica", "Valor")
                    .withRecordSeparator("\n");

            CSVPrinter csvPrinter = new CSVPrinter(sw, format);

            csvPrinter.printRecord("Data", data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            csvPrinter.printRecord("Receita Total", "R$ " + String.format("%.2f", relatorio.receitaTotal()));
            csvPrinter.printRecord("Quantidade de Veículos", relatorio.quantidade());
            csvPrinter.printRecord("Tempo Médio (horas)", String.format("%.2f", relatorio.tempoMedioHoras()));
            csvPrinter.printRecord("Ocupação Média", String.format("%.2f%%", relatorio.ocupacaoMedia() * 100));

            csvPrinter.flush();
            csvPrinter.close();

            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }

    public byte[] gerarRelatorioPDF(LocalDate data) {
        try {
            RelatorioDTO relatorio = gerarRelatorioDesempenho(data);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            Paragraph title = new Paragraph("Relatório Diário de Desempenho")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            Paragraph dateP = new Paragraph("Data: " + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setFont(font)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(dateP);

            Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Métrica").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Valor").setFont(boldFont)));

            table.addCell(new Cell().add(new Paragraph("Receita Total").setFont(font)));
            table.addCell(new Cell().add(new Paragraph("R$ " + String.format("%.2f", relatorio.receitaTotal())).setFont(font)));

            table.addCell(new Cell().add(new Paragraph("Quantidade de Veículos").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(relatorio.quantidade())).setFont(font)));

            table.addCell(new Cell().add(new Paragraph("Tempo Médio (horas)").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", relatorio.tempoMedioHoras())).setFont(font)));

            table.addCell(new Cell().add(new Paragraph("Ocupação Média").setFont(font)));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f%%", relatorio.ocupacaoMedia() * 100)).setFont(font)));

            document.add(table);

            Paragraph footer = new Paragraph("\nRelatório gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setFont(font)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
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

    public Map<String, Object> gerarRelatorioMensal(int mes, int ano) {
        LocalDate inicioMes = LocalDate.of(ano, mes, 1);
        LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        double receitaTotal = 0;
        int totalVeiculos = 0;
        double tempoTotalMinutos = 0;
        Map<Integer, Double> receitaPorDia = new HashMap<>();
        Map<Integer, Integer> veiculosPorDia = new HashMap<>();

        for (LocalDate data = inicioMes; !data.isAfter(fimMes); data = data.plusDays(1)) {
            RelatorioDTO relatorioDia = gerarRelatorioDesempenho(data);

            int dia = data.getDayOfMonth();
            receitaPorDia.put(dia, relatorioDia.receitaTotal());
            veiculosPorDia.put(dia, relatorioDia.quantidade());

            receitaTotal += relatorioDia.receitaTotal();
            totalVeiculos += relatorioDia.quantidade();
            tempoTotalMinutos += (relatorioDia.tempoMedioHoras() * relatorioDia.quantidade() * 60);
        }

        double tempoMedioHoras = totalVeiculos > 0 ? (tempoTotalMinutos / totalVeiculos) / 60.0 : 0;
        double receitaMediaDiaria = receitaTotal / inicioMes.lengthOfMonth();
        double veiculosMediaDiaria = (double) totalVeiculos / inicioMes.lengthOfMonth();

        int melhorDia = receitaPorDia.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(1);

        double melhorReceita = receitaPorDia.getOrDefault(melhorDia, 0.0);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("mes", mes);
        resultado.put("ano", ano);
        resultado.put("receitaTotal", receitaTotal);
        resultado.put("totalVeiculos", totalVeiculos);
        resultado.put("tempoMedioHoras", tempoMedioHoras);
        resultado.put("receitaMediaDiaria", receitaMediaDiaria);
        resultado.put("veiculosMediaDiaria", veiculosMediaDiaria);
        resultado.put("melhorDia", melhorDia);
        resultado.put("melhorReceita", melhorReceita);
        resultado.put("receitaPorDia", receitaPorDia);
        resultado.put("veiculosPorDia", veiculosPorDia);
        resultado.put("diasNoMes", inicioMes.lengthOfMonth());

        return resultado;
    }

    public byte[] gerarRelatorioMensalPDF(int mes, int ano) {
        try {
            Map<String, Object> dados = gerarRelatorioMensal(mes, ano);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            Paragraph title = new Paragraph("Relatório Mensal de Desempenho")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            String[] meses = {"", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
            Paragraph periodo = new Paragraph("Período: " + meses[mes] + " de " + ano)
                    .setFont(font)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(periodo);

            Table resumoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(100));

            resumoTable.addHeaderCell(new Cell().add(new Paragraph("Métrica").setFont(boldFont)));
            resumoTable.addHeaderCell(new Cell().add(new Paragraph("Valor").setFont(boldFont)));

            resumoTable.addCell(new Cell().add(new Paragraph("Receita Total").setFont(font)));
            resumoTable.addCell(new Cell().add(new Paragraph("R$ " + String.format("%.2f", dados.get("receitaTotal"))).setFont(font)));

            resumoTable.addCell(new Cell().add(new Paragraph("Total de Veículos").setFont(font)));
            resumoTable.addCell(new Cell().add(new Paragraph(dados.get("totalVeiculos").toString()).setFont(font)));

            resumoTable.addCell(new Cell().add(new Paragraph("Tempo Médio (horas)").setFont(font)));
            resumoTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", dados.get("tempoMedioHoras"))).setFont(font)));

            resumoTable.addCell(new Cell().add(new Paragraph("Receita Média Diária").setFont(font)));
            resumoTable.addCell(new Cell().add(new Paragraph("R$ " + String.format("%.2f", dados.get("receitaMediaDiaria"))).setFont(font)));

            resumoTable.addCell(new Cell().add(new Paragraph("Melhor Dia").setFont(font)));
            resumoTable.addCell(new Cell().add(new Paragraph("Dia " + dados.get("melhorDia") + " (R$ " + String.format("%.2f", dados.get("melhorReceita")) + ")").setFont(font)));

            document.add(resumoTable);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF mensal", e);
        }
    }
}
