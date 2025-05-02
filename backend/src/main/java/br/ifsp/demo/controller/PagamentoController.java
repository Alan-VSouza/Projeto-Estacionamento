package br.ifsp.demo.controller;

import br.ifsp.demo.exception.ErrorResponse;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.service.PagamentoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/pagamento")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<Object> criarPagamento(@RequestBody Pagamento pagamento) {
        if (pagamento.getVeiculo() == null) {
            ErrorResponse errorResponse = new ErrorResponse("Veículo não pode ser nulo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if(pagamento.getHoraSaida() == null) {
            ErrorResponse errorResponse = new ErrorResponse("Hora de saída não pode ser nula");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if(pagamento.getHoraEntrada() == null) {
            ErrorResponse errorResponse = new ErrorResponse("Hora de entrada não pode ser nula");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if(pagamento.getHoraEntrada().isAfter(pagamento.getHoraSaida())) {
            ErrorResponse errorResponse = new ErrorResponse("Hora de saída não pode estar antes da hora de entrada");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            pagamentoService.salvarPagamento(pagamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluirPagamento(@PathVariable UUID id) {

        if(id == null) {
            ErrorResponse errorResponse = new ErrorResponse("Id do pagamento não pode ser nulo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            Pagamento pagamento = pagamentoService.buscarPorId(id);
            pagamentoService.deletarPagamento(pagamento);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch(Exception e) {

            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        }

    }

}
