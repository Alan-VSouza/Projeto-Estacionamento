package br.ifsp.demo.controller;

import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/desempenho")
    public ResponseEntity<RelatorioDTO> gerarRelatorioDesempenho(@RequestParam("data")
                                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioDesempenho(data));
    }

    @GetMapping("/recibo")
    public ResponseEntity<ReciboDTO> gerarRecibo(@RequestParam("placa") String placa) {
        ReciboDTO recibo = relatorioService.gerarRecibo(placa);
        if (recibo != null) {
            return ResponseEntity.ok(recibo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/historico/{placa}")
    public ResponseEntity<List<HistoricoDTO>> gerarHistoricoPorPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(relatorioService.gerarHistorico(placa));
    }

    @GetMapping("/vagas-disponiveis")
    public ResponseEntity<Map<String, Integer>> vagasDisponiveis() {
        return ResponseEntity.ok(Map.of("vagasDisponiveis", relatorioService.vagasDisponiveis()));
    }

    @GetMapping("/vagas-ocupadas")
    public ResponseEntity<Map<String, Integer>> vagasOcupadas() {
        return ResponseEntity.ok(Map.of("vagasOcupadas", relatorioService.vagasOcupadas()));
    }

    @GetMapping("/desempenho/export/csv")
    public ResponseEntity<String> exportarRelatorioCSV(@RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        String csvContent = relatorioService.gerarRelatorioCSV(data); // Chama o método do RelatorioService

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "relatorio-" + data + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }
}
