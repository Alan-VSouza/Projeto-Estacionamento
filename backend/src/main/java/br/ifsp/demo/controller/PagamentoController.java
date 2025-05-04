package br.ifsp.demo.controller;

import br.ifsp.demo.exception.ErrorResponse;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.service.PagamentoService;
import br.ifsp.demo.service.VeiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final VeiculoService veiculoService;

    public PagamentoController(PagamentoService pagamentoService, VeiculoService veiculoService) {
        this.pagamentoService = pagamentoService;
        this.veiculoService = veiculoService;
    }

    @PostMapping
    public ResponseEntity<Object> criarPagamento(@RequestBody Pagamento pagamento) {

        if (veiculoService.buscarPorPlaca(pagamento.getPlaca()).isEmpty()) {
            ErrorResponse errorResponse = new ErrorResponse("Veículo precisa estar registrado no banco");
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

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarPagamento(@PathVariable UUID id, @RequestBody Pagamento pagamentoAtualizado) {

        if(id == null) {
            ErrorResponse errorResponse = new ErrorResponse("Id do pagamento não pode ser nulo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if(pagamentoAtualizado == null) {
            ErrorResponse errorResponse = new ErrorResponse("Pagamento atualizado não pode ser nulo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            Pagamento pagamento = pagamentoService.atualizarPagamento(
                    pagamentoAtualizado.getUuid(),
                    pagamentoAtualizado.getHoraEntrada(),
                    pagamentoAtualizado.getHoraSaida(),
                    pagamentoAtualizado.getPlaca(),
                    pagamentoAtualizado.getValor()
            );
            return ResponseEntity.ok(pagamento);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPagamentoPorId(@PathVariable UUID id) {

        if(id == null) {
            ErrorResponse errorResponse = new ErrorResponse("Id do pagamento não pode ser nulo");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            Pagamento pagamento = pagamentoService.buscarPorId(id);
            return ResponseEntity.ok(pagamento);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }

    }

    @GetMapping("/data")
    public ResponseEntity<Object> buscarPagamentoPorData(@RequestParam("data") String data) {

        if(data == null || data.isBlank()) {
            ErrorResponse errorResponse = new ErrorResponse("Data não pode ser nula nem vazia");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        try {
            LocalDate dataFormatada = LocalDate.parse(data);
            var pagamentos = pagamentoService.buscarPorData(dataFormatada);

            if(pagamentos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Nenhum pagamento encontrado nessa data"));
            }

            return ResponseEntity.ok(pagamentos);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }

    }

}
