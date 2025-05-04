package br.ifsp.demo.controller;

import br.ifsp.demo.exception.ErrorResponse;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.service.PagamentoService;
import br.ifsp.demo.service.VeiculoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@RestController
@RequestMapping("/api/pagamentos")
@AllArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final VeiculoService veiculoService;

    @PostMapping
    public ResponseEntity<Object> criarPagamento(@RequestBody Pagamento pagamento) {
        if (veiculoService.buscarPorPlaca(pagamento.getPlaca()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Veículo precisa estar registrado no banco"));
        }

        if(pagamento.getHoraSaida() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Hora de saída não pode ser nula"));
        }

        if(pagamento.getHoraEntrada() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Hora de entrada não pode ser nula"));
        }

        if(pagamento.getHoraEntrada().isAfter(pagamento.getHoraSaida())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Hora de saída não pode estar antes da hora de entrada"));
        }

        try {
            pagamentoService.salvarPagamento(pagamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluirPagamento(@PathVariable UUID id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Id do pagamento não pode ser nulo"));
        }

        try {
            Pagamento pagamento = pagamentoService.buscarPorId(id);
            if (pagamento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Pagamento não encontrado"));
            }

            pagamentoService.deletarPagamento(pagamento);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarPagamento(@PathVariable UUID id, @RequestBody Pagamento pagamentoAtualizado) {
        if(id == null || pagamentoAtualizado == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Id ou pagamento não pode ser nulo"));
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPagamentoPorId(@PathVariable UUID id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Id do pagamento não pode ser nulo"));
        }

        try {
            Pagamento pagamento = pagamentoService.buscarPorId(id);
            if (pagamento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Pagamento não encontrado"));
            }
            return ResponseEntity.ok(pagamento);
        } catch (Exception e) {
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