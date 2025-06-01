package br.ifsp.demo.controller;

import br.ifsp.demo.dto.CriarEstacionamentoDTO;
import br.ifsp.demo.dto.HistoricoDTO;
import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.dto.VeiculoComVagaDTO;
import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.EstacionamentoService;
import br.ifsp.demo.service.RelatorioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/estacionamento")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;
    private final RelatorioService relatorioService;

    @PostMapping("/registar-entrada")
    public ResponseEntity<RegistroEntrada> registrarEntrada(@Valid @RequestBody VeiculoComVagaDTO request) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoAtual();

        Integer vagaId = request.vagaId();
        if (vagaId == null) {
            vagaId = estacionamentoService.findNextAvailableSpot();
        }

        Veiculo veiculo = new Veiculo(
                request.placa(),
                request.tipoVeiculo(),
                request.modelo(),
                request.cor()
        );

        RegistroEntrada registro = estacionamentoService.registrarEntrada(veiculo, estacionamento.getId(), vagaId);
        return ResponseEntity.ok(registro);
    }

    @PostMapping("/cancelar-entrada")
    public ResponseEntity<Void> cancelarEntrada(@RequestParam String placa) {
        estacionamentoService.cancelarEntrada(placa);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/registrar-saida")
    public ResponseEntity<ReciboDTO> registrarSaida(@RequestParam("placa") String placa) {
        Pagamento pagamento = estacionamentoService.registrarSaida(placa);

        ReciboDTO recibo = new ReciboDTO(
                pagamento.getPlaca(),
                pagamento.getHoraEntrada(),
                pagamento.getHoraSaida(),
                pagamento.getValor()
        );

        return ResponseEntity.ok(recibo);
    }

    @GetMapping("/buscar-entrada")
    public ResponseEntity<RegistroEntrada> buscarEntrada(@RequestParam("placa") String placa) {
        RegistroEntrada registro = estacionamentoService.buscarEntrada(placa);
        return registro != null
                ? ResponseEntity.ok(registro)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/entradas")
    public ResponseEntity<List<RegistroEntrada>> getAllEntries() {
        List<RegistroEntrada> entradas = estacionamentoService.getAllEntradas();
        return ResponseEntity.ok(entradas);
    }

    @PostMapping("/criar-estacionamento")
    public ResponseEntity<Estacionamento> criar(@Valid @RequestBody CriarEstacionamentoDTO estacionamento) {
        Estacionamento criado = estacionamentoService.criarEstacionamento(estacionamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/buscar-estacionamento/{id}")
    public ResponseEntity<Estacionamento> buscar(@PathVariable UUID id) {
        Estacionamento est = estacionamentoService.buscarEstacionamento(id);
        return ResponseEntity.ok(est);
    }

    @GetMapping("/buscar-atual-estacionamento")
    public ResponseEntity<Estacionamento> buscarEstacionamento() {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoAtual();
        return ResponseEntity.ok(estacionamento);
    }

    @GetMapping("/vagas-disponiveis")
    public ResponseEntity<Integer> getAvailableSpots() {
        int vagasDisponiveis = relatorioService.vagasDisponiveis();
        return ResponseEntity.ok(vagasDisponiveis);
    }

    @GetMapping("/vagas-ocupadas")
    public ResponseEntity<Integer> getOccupiedSpots() {
        int vagasOcupadas = relatorioService.vagasOcupadas();
        return ResponseEntity.ok(vagasOcupadas);
    }

    @GetMapping("/relatorios/desempenho")
    public ResponseEntity<RelatorioDTO> gerarRelatorioDesempenho(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        RelatorioDTO relatorio = relatorioService.gerarRelatorioDesempenho(data);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/relatorios/historico/{placa}")
    public ResponseEntity<List<HistoricoDTO>> getVehicleHistory(@PathVariable String placa) {
        List<HistoricoDTO> historico = relatorioService.gerarHistorico(placa);

        if (historico.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(historico);
    }

    @GetMapping("/relatorios/estatisticas")
    public ResponseEntity<Map<String, Object>> getEstatisticasTempoReal() {
        Map<String, Object> estatisticas = new HashMap<>();

        estatisticas.put("vagasDisponiveis", relatorioService.vagasDisponiveis());
        estatisticas.put("vagasOcupadas", relatorioService.vagasOcupadas());
        estatisticas.put("totalVagas", 200);

        RelatorioDTO relatorioHoje = relatorioService.gerarRelatorioDesempenho(LocalDate.now());
        estatisticas.put("receitaHoje", relatorioHoje.receitaTotal());
        estatisticas.put("veiculosAtendidosHoje", relatorioHoje.quantidade());
        estatisticas.put("tempoMedioHoje", relatorioHoje.tempoMedioHoras());
        estatisticas.put("ocupacaoMediaHoje", relatorioHoje.ocupacaoMedia());

        double taxaOcupacaoAtual = (double) relatorioService.vagasOcupadas() / 200 * 100;
        estatisticas.put("taxaOcupacaoAtual", taxaOcupacaoAtual);

        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/relatorios/estatisticas/semanal")
    public ResponseEntity<Map<String, Object>> getEstatisticasSemanais() {
        Map<String, Object> estatisticas = new HashMap<>();

        LocalDate hoje = LocalDate.now();
        double receitaSemanal = 0;
        int veiculosSemanal = 0;
        double tempoTotalSemanal = 0;
        int diasComDados = 0;

        for (int i = 0; i < 7; i++) {
            LocalDate data = hoje.minusDays(i);
            RelatorioDTO relatorio = relatorioService.gerarRelatorioDesempenho(data);

            receitaSemanal += relatorio.receitaTotal();
            veiculosSemanal += relatorio.quantidade();

            if (relatorio.quantidade() > 0) {
                tempoTotalSemanal += (relatorio.tempoMedioHoras() * relatorio.quantidade());
                diasComDados++;
            }
        }

        estatisticas.put("receitaSemanal", receitaSemanal);
        estatisticas.put("veiculosSemanal", veiculosSemanal);

        double tempoMedioSemanal = veiculosSemanal > 0 ? tempoTotalSemanal / veiculosSemanal : 0;
        estatisticas.put("tempoMedioSemanal", tempoMedioSemanal);

        estatisticas.put("mediaDiariaReceita", receitaSemanal / 7);
        estatisticas.put("mediaDiariaVeiculos", veiculosSemanal / 7.0);

        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/relatorios/desempenho/export/csv")
    public ResponseEntity<Resource> exportarRelatorioCSV(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
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

    @GetMapping("/relatorios/desempenho/export/pdf")
    public ResponseEntity<Resource> exportarRelatorioPDF(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
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
