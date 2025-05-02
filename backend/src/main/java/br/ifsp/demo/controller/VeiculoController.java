package br.ifsp.demo.controller;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.exception.ErrorResponse;
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
    public ResponseEntity<Object> cadastrarVeiculo(@RequestBody Veiculo veiculo) {
        ResponseEntity<Object> valid = validarCampos(veiculo, true);
        if (valid != null) return valid;

        if (veiculoService.buscarPorPlaca(veiculo.getPlaca()).isPresent()) {
            return erro("Placa já cadastrada", HttpStatus.BAD_REQUEST);
        }

        try {
            Veiculo saved = veiculoService.cadastrarVeiculo(
                    veiculo.getPlaca(), veiculo.getHoraEntrada(),
                    veiculo.getTipoVeiculo(), veiculo.getModelo(), veiculo.getCor());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return erro(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarVeiculo(@PathVariable Long id,
                                                   @RequestBody Veiculo veiculo) {
        ResponseEntity<Object> validacao = validarCampos(veiculo, false);
        if (validacao != null) {
            return validacao;
        }

        Veiculo veiculoExistente = veiculoService.atualizarVeiculo(
                id,
                veiculo.getPlaca(),
                veiculo.getTipoVeiculo(),
                veiculo.getModelo(),
                veiculo.getCor()
        );

        if (veiculoExistente == null) {
            return erro("Veículo não encontrado", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(veiculoExistente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarVeiculo(@PathVariable Long id) {
        try {
            veiculoService.deletarVeiculo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return erro(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> buscarPorPlaca(@RequestParam String placa) {
        return veiculoService.buscarPorPlaca(placa)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponse("Veículo não encontrado"))
                );
    }

    private ResponseEntity<Object> validarCampos(Veiculo veiculo, boolean validarTipo) {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            return erro("Placa não pode ser vazia", HttpStatus.BAD_REQUEST);
        }
        if (validarTipo && (veiculo.getTipoVeiculo() == null || veiculo.getTipoVeiculo().trim().isEmpty())) {
            return erro("Tipo de veículo não pode ser vazio", HttpStatus.BAD_REQUEST);
        }
        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            return erro("Modelo não pode ser vazio", HttpStatus.BAD_REQUEST);
        }
        if (veiculo.getCor() == null || veiculo.getCor().trim().isEmpty()) {
            return erro("Cor não pode ser vazia", HttpStatus.BAD_REQUEST);
        }
        if (veiculo.getHoraEntrada() == null) {
            return erro("Hora de entrada não pode ser nula", HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    private ResponseEntity<Object> erro(String mensagem, HttpStatus status) {
        return ResponseEntity.status(status).body(new ErrorResponse(mensagem));
    }
}
