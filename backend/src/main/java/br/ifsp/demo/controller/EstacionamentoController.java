package br.ifsp.demo.controller;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/estacionamento")
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;

    @Autowired
    public EstacionamentoController(EstacionamentoService estacionamentoService) {
        this.estacionamentoService = estacionamentoService;
    }

    @PostMapping("/registar-entrada")
    public ResponseEntity<RegistroEntrada> registrarEntrada(@RequestBody Veiculo veiculo) {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoAtual();

        RegistroEntrada registro = estacionamentoService.registrarEntrada(veiculo, estacionamento.getId());
        return ResponseEntity.ok(registro);
    }

    @DeleteMapping("/cancelar-entrada")
    public ResponseEntity<Void> cancelarEntrada(@RequestParam("placa") String placa) {
        boolean sucesso = estacionamentoService.cancelarEntrada(placa);
        if (sucesso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/registrar-saida")
    public ResponseEntity<Void> registrarSaida(@RequestParam("placa") String placa) {
        boolean sucesso = estacionamentoService.registrarSaida(placa);
        if (sucesso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/buscar-entrada")
    public ResponseEntity<RegistroEntrada> buscarEntrada(@RequestParam("placa") String placa) {
        RegistroEntrada registro = estacionamentoService.buscarEntrada(placa);
        if (registro != null) {
            return ResponseEntity.ok(registro);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/criar-estacionamento")
    public ResponseEntity<Estacionamento> criar(@RequestBody Estacionamento estacionamento) {
        Estacionamento criado = estacionamentoService.criarEstacionamento(estacionamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/buscar-estacionamento/{id}")
    public ResponseEntity<Estacionamento> buscar(@PathVariable UUID id) {
        var est = estacionamentoService.buscarEstacionamento(id);
        return ResponseEntity.ok(est);
    }

    @GetMapping("/buscar-atual-estacionamento")
    public ResponseEntity<Estacionamento> buscarEstacionamento() {
        Estacionamento estacionamento = estacionamentoService.buscarEstacionamentoAtual();
        return ResponseEntity.ok(estacionamento);
    }

}
