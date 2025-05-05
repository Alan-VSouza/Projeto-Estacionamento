package br.ifsp.demo.controller;

import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @Autowired
    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/desempenho")
    public ResponseEntity<RelatorioDTO> gerarRelatorioDesempenho(@RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        RelatorioDTO relatorio = relatorioService.gerarRelatorioDesempenho(data);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/recibo")
    public ResponseEntity<ReciboDTO> gerarRecibo(@RequestParam("placa") String placa) {
        ReciboDTO recibo = relatorioService.gerarRecibo(placa);
        if (recibo == null) {return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(recibo);
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
}