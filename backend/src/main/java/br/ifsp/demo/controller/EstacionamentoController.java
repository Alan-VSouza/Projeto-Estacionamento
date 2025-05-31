package br.ifsp.demo.controller;

import br.ifsp.demo.dto.CriarEstacionamentoDTO;
import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.EstacionamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estacionamento")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;

    @PostMapping("/registar-entrada")
    public ResponseEntity<RegistroEntrada> registrarEntrada(@RequestBody Veiculo  veiculo) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoAtual();
        RegistroEntrada registro = estacionamentoService.registrar(veiculo, estacionamento.getId());
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

    @GetMapping("/entradas")
    public ResponseEntity<List<RegistroEntrada>> getAllEntries() {
        List<RegistroEntrada> entradas = estacionamentoService.getAllEntradas();
        return ResponseEntity.ok(entradas);
    }
}
