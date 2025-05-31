package br.ifsp.demo.service;

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

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
