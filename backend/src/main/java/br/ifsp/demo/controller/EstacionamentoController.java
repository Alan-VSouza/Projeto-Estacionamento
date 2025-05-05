package br.ifsp.demo.controller;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.EstacionamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estacionamento")
@RequiredArgsConstructor
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;

    @PostMapping("/registar-entrada")
    public ResponseEntity<RegistroEntrada> registrarEntrada(@RequestBody Veiculo veiculo) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoAtual();
        RegistroEntrada registro = estacionamentoService.registrarEntrada(veiculo, estacionamento.getId());
        return ResponseEntity.ok(registro);
    }

    @DeleteMapping("/cancelar-entrada")
    public ResponseEntity<Void> cancelarEntrada(@RequestParam("placa") String placa) {
        return estacionamentoService.cancelarEntrada(placa)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/registrar-saida")
    public ResponseEntity<Void> registrarSaida(@RequestParam("placa") String placa) {
        return estacionamentoService.registrarSaida(placa)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar-entrada")
    public ResponseEntity<RegistroEntrada> buscarEntrada(@RequestParam("placa") String placa) {
        RegistroEntrada registro = estacionamentoService.buscarEntrada(placa);
        return registro != null
                ? ResponseEntity.ok(registro)
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/criar-estacionamento")
    public ResponseEntity<Estacionamento> criar(@RequestBody Estacionamento estacionamento) {
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
