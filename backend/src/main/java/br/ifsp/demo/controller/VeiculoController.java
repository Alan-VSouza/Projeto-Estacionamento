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
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Placa não pode ser vazia");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Modelo não pode ser vazio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (veiculo.getCor() == null || veiculo.getCor().trim().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Cor não pode ser vazia");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (veiculo.getHoraEntrada() == null) {
            ErrorResponse errorResponse = new ErrorResponse("Hora de entrada não pode ser nula");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (veiculoService.verificarPlacaCadastrada(veiculo.getPlaca())) {
            ErrorResponse errorResponse = new ErrorResponse("Placa já cadastrada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            Veiculo veiculoCadastrado = veiculoService.cadastrarVeiculo(
                    veiculo.getPlaca(), veiculo.getHoraEntrada(),
                    veiculo.getTipoVeiculo(), veiculo.getModelo(), veiculo.getCor());
            return ResponseEntity.status(HttpStatus.CREATED).body(veiculoCadastrado);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarVeiculo(@PathVariable Long id, @RequestBody Veiculo veiculo) {
        if (veiculo.getPlaca() == null || veiculo.getPlaca().trim().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Placa não pode ser vazia");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (veiculo.getModelo() == null || veiculo.getModelo().trim().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Modelo não pode ser vazio");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (veiculo.getCor() == null || veiculo.getCor().trim().isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Cor não pode ser vazia");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (veiculo.getHoraEntrada() == null) {
            ErrorResponse errorResponse = new ErrorResponse("Hora de entrada não pode ser nula");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Veiculo veiculoExistente = veiculoService.atualizarVeiculo(id, veiculo.getPlaca(),
                veiculo.getTipoVeiculo(), veiculo.getModelo(), veiculo.getCor());

        if (veiculoExistente == null) {
            ErrorResponse errorResponse = new ErrorResponse("Veículo não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return ResponseEntity.status(HttpStatus.OK).body(veiculoExistente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarVeiculo(@PathVariable Long id) {
        try {
            veiculoService.deletarVeiculo(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


}
