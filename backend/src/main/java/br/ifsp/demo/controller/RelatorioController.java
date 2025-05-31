package br.ifsp.demo.controller;

import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "http://localhost:3000")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Autowired
    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/desempenho")
    public ResponseEntity<RelatorioDTO> gerarRelatorioDesempenho(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        RelatorioDTO relatorio = relatorioService.gerarRelatorioDesempenho(data);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/desempenho/export/csv")
    public ResponseEntity<Resource> exportarRelatorioCSV(@RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            String csvContent = relatorioService.gerarRelatorioCSV(data);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);
            baos.write(csvContent.getBytes(StandardCharsets.UTF_8));

            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-" + data + ".csv")
                    .header(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8")
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar CSV", e);
        }
    }

    @GetMapping("/desempenho/export/pdf")
    public ResponseEntity<Resource> exportarRelatorioPDF(@RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            byte[] pdfContent = relatorioService.gerarRelatorioPDF(data);

            ByteArrayResource resource = new ByteArrayResource(pdfContent);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-" + data + ".pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }
}
