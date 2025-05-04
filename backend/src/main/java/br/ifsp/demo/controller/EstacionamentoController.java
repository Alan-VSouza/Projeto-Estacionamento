package br.ifsp.demo.controller;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.EstacionamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estacionamento")
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;

    @Autowired
    public EstacionamentoController(EstacionamentoService estacionamentoService) {
        this.estacionamentoService = estacionamentoService;
    }

    @PostMapping("/entrada")
    public ResponseEntity<RegistroEntrada> registrarEntrada(@RequestBody Veiculo veiculo) {
        RegistroEntrada registro = estacionamentoService.registrarEntrada(veiculo);
        return ResponseEntity.ok(registro);
    }

    @DeleteMapping("/entrada")
    public ResponseEntity<Void> cancelarEntrada(@RequestBody Veiculo veiculo) {
        boolean sucesso = estacionamentoService.cancelarEntrada(veiculo);
        if (sucesso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/saida")
    public ResponseEntity<Void> registrarSaida(@RequestBody Veiculo veiculo) {
        boolean sucesso = estacionamentoService.registrarSaida(veiculo);
        if (sucesso) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
