package br.ifsp.demo.controller;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.VeiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    public ResponseEntity<Veiculo> cadastrarVeiculo(@RequestBody Veiculo veiculo) {
        Veiculo veiculoCadastrado = veiculoService.cadastrarVeiculo(
                veiculo.getPlaca(), veiculo.getHoraEntrada(),
                veiculo.getTipoVeiculo(), veiculo.getModelo(), veiculo.getCor());

        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoCadastrado);
    }

}
