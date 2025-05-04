package br.ifsp.demo.controller;

import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
}